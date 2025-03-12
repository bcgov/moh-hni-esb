#!/bin/bash
# This is a one of script to retroactively archive files as if the script hnsesb-filedrops-cleanup.sh had been run from 
# when the hns-esb app had been deployed. It will step day by day from 90 (the oldest possible unarchived files, to 11 earliest a 
# should have been archived. The script hnsesb-filedrops-cleanup.sh will then be set to run nightly in a job to continue the process.
	
#Batch size constant - limits number of files in each archive. 
declare -i BATCH_SIZE=1000

##------------------------------------------------------------------##
#function for archiving and compressing files. Files are compressed in batches and the zipped files are moved to the archive directory.
zipLogs() {
  
    #Function arguments
    declare -i NUM_DAYS_TILL_ARCHIVE=$1
	DIRECTORY=$2
	
    echo "Archiving files older than $NUM_DAYS_TILL_ARCHIVE in: $(pwd)"	

	#set timestamp for batch label
    stampDate=$(( $NUM_DAYS_TILL_ARCHIVE + 1 ))
    RUN_DATE=$(date -d "-$stampDate days" +'%Y%m%d%H%M')
	
	#Proceed with archiving - sanity check for filedrop directory
	currDir=${PWD##*/}
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

for (( FILE_DAYS = 30; FILE_DAYS >= 11; FILE_DAYS--))
do
	echo "Calling zipLogs for $FILE_DAYS"
	zipLogs $FILE_DAYS /tmp/logs/filedrops	
done	


