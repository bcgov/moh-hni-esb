#!/bin/bash

## Bash script for the process of cleaning up the filedrops directory. The filedrops directory is ##
## used by HNS-ESB for storing incoming and outgoing messages. Cleanup process has two stages: ##
## - Achive files older than X number of days ##
## - Remove archives older than Y number of days ##
## This process will be run on a schedule using an OpenShift cron job. Recommended schedule is daily outside peak hours e.g. 02:00: ## 
## 0 2 * * * ##

#Constants for keeping filedrops and log files
declare -i ARCHIVE_AFTER_DAYS=10 # ~1week

#Constants for keeping filedrops and log files
declare -i REMOVE_AFTER_DAYS=90 # ~1week

#Batch size constant - limits number of files in each archive. 
declare -i BATCH_SIZE=1000
##------------------------------------------------------------------##
#function for archiving and compressing files. Files are compressed in batches and the zipped files are moved to the archive directory.
zipLogs() {
  
    #Function arguments
    declare -i NUM_DAYS_TILL_ARCHIVE=$1
	DIRECTORY=$2
	
    echo "Archiving in: $(pwd)"	

	#set timestamp for batch label
    stampDate=$(( $NUM_DAYS_TILL_ARCHIVE + 1 ))
    RUN_DATE=$(date -d "-$stampDate days" +'%Y%m%d%H%M')
	
	#Proceed with archiving - sanity check for filedrop directory
	currDir=${PWD##*/}
	echo $currDir
    if [ $currDir == "filedrops" ]; then

		FILES=($(find $DIRECTORY -maxdepth 1 -type f -mtime +$NUM_DAYS_TILL_ARCHIVE))
		
		#archive if files are found
        if [ "$FILES" != "" ]; then
			
		    #batching vars 
		    declare -i numFiles=${#FILES[@]}		
    		declare -a BATCH
    		declare -i batchNum=0
    		declare -i batchLabel=1
    		
			echo "Archiving $numFiles files"

    		#create archival directory if it does not already exist
    		if ! [ -d "$DIRECTORY/archive" ]; then	
    			echo "Making new archive directory in $(pwd)"
    			mkdir -v $DIRECTORY/archive
    		fi
    		
    		while [ $batchNum -lt $numFiles ] 
    		do
    			#Batch files and strip path from filename
    			for (( i = 0; i < BATCH_SIZE ; i++)) 
    			do
    				#echo "Batching: " ${FILES[$batchNum]}
					BATCH+=($(basename ${FILES[$batchNum]}))
    				let "batchNum++"
    				
    				#break out of loop if we are at the end of the array
    				if [ $batchNum -ge $numFiles ]; then
    					break
    				fi
    			done

	    		#archive files
                echo "Archiving Batch $batchLabel, ${#BATCH[@]} entries older than $NUM_DAYS_TILL_ARCHIVE days in $(pwd)"
	    		tar cfz $DIRECTORY/archive/$RUN_DATE.Batch$batchLabel.tar.gz -C $DIRECTORY "${BATCH[@]}" --remove-files
			
		    	#ready for a new batch
		    	BATCH=()
		    	let "batchLabel++"
		    done
		
         else
            echo "No files to archive in $DIRECTORY"
         fi
	else 
		echo "Not in filedrops directory"
    fi
	
    echo "Finished archiving in: $(pwd)"	
}
##------------------------------------------------------------------##
#function for removing older zipped archives
removeOldArchives() {

    #Function arguments
    declare -i NUM_DAYS_TILL_REMOVAL=$1
	DIRECTORY=$2
	
    echo "Removing old zipped archives from: $(pwd)"	

	echo "Removing $(find $DIRECTORY/archive -mtime +$NUM_DAYS_TILL_REMOVAL -type f \( -name "*.tar.gz" \) -print | wc -l) files older than $NUM_DAYS_TILL_REMOVAL days $DIRECTORY/archive"
	find $DIRECTORY/archive -mtime +$NUM_DAYS_TILL_REMOVAL -type f \( -name "*.tar.gz" \) -exec rm {} \;
	
    echo "Older zipped archives have been removed from: $(pwd)"	
}

##------------------------------------------------------------------##
#Function to archive and zip files and issue call to remove older archives
processFiledrops() {
	
	#Function arguments
    declare -i NUM_DAYS_TILL_ARCHIVE=$1	
    declare -i NUM_DAYS_TILL_REMOVAL=$2
    DIRECTORY=$3

    #check for correct number of arguments
    if (($# !="3")); then
        echo "You must call the script function zip() with a parameter:"
        echo " 1 - Zip entries older than X days"
		echo " 2 - Remove zipped archives older than Y days"
        echo " 3 - Full path to parent directory location to begin zipping from"
        return 1
    fi
	
    echo "Call zipLogs"	
	cd $DIRECTORY
	zipLogs $NUM_DAYS_TILL_ARCHIVE $DIRECTORY
	
    echo "Call removeOldArchives"	
	cd ./archive
	removeOldArchives $NUM_DAYS_TILL_REMOVAL $DIRECTORY
    echo ""
}

##------------------------------------------------------------------##
#function for removing older orphaned log files as these are not handled by the log4j config
removeOrphanedLogFiles() {

    #Function arguments
    declare -i NUM_DAYS_TILL_REMOVAL=$1
	DIRECTORY=$2
	
	cd $DIRECTORY
	
    echo "Removing old orphaned log files from: $(pwd)"	

	echo "Removing $(find $DIRECTORY -mtime +$NUM_DAYS_TILL_REMOVAL -type f \( -name "*.log" \) -print | wc -l) files older than $NUM_DAYS_TILL_REMOVAL days $DIRECTORY"
	find $DIRECTORY -mtime +$NUM_DAYS_TILL_REMOVAL -type f \( -name "*.log" \) -exec rm {} \;
	
    echo "Older orphaned log files have been removed from: $(pwd)"	
}

##------------------------------------------------------------------##
#echo date and time into file
echo `date`
echo ""
##------------------------------------------------------------------##
##** PROCESS FILEDROPS **##

#Process filedrop directory
processFiledrops $ARCHIVE_AFTER_DAYS $REMOVE_AFTER_DAYS /tmp/logs/filedrops

removeOrphanedLogFiles $REMOVE_AFTER_DAYS /tmp/logs/

echo "--------------------------------------"
