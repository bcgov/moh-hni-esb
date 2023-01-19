package ca.bc.gov.hlth.hnsecure.filedrops;

import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.FILE_DROPS_LOCATION;
import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.FILE_DROPS_ROTATION_DELETE_AFTER;

import java.io.File;
import java.io.FileFilter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hncommon.util.LoggingUtil;
import ca.bc.gov.hlth.hnsecure.audit.persistence.AbstractAuditPersistence;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;

/**
 * Processor to handle file rotation policy. Currently this does a cleanup on logged request/response messages
 *
 */
public class RotateFilesProcessor extends AbstractAuditPersistence implements Processor {

	private static Logger logger = LoggerFactory.getLogger(RotateFilesProcessor.class);

	private static final ApplicationProperties properties = ApplicationProperties.getInstance();
	
	@Override
	public void process(Exchange exchange) {
		String methodName = LoggingUtil.getMethodName();

		logger.info("{} - Begin", methodName);
		
		//Delete logged request/response files older than specified number of days
		
		String fileDropLocation = properties.getValue(FILE_DROPS_LOCATION);
		int deleteAfterDays = Integer.parseInt(properties.getValue(FILE_DROPS_ROTATION_DELETE_AFTER));
		logger.info("Delete files from: {}; after: {} days.", fileDropLocation, deleteAfterDays);
		
		// Set a cut off date for file deletion to be the end of the day before the specified number of days to keep the files.
        LocalDateTime cutoffLocalDateTime = LocalDate.now().minusDays(deleteAfterDays + 1).atTime(LocalTime.MAX);        
        Date cutoffDate = Date.from(cutoffLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
		logger.info("Delete files older than: {}.", cutoffDate);

        File fileDropDirectory = new File(fileDropLocation);
		long fileDropDirectorySizeBefore = FileUtils.sizeOfDirectory(fileDropDirectory);
		logger.info("File drop directory size before cleanup: {}.", fileDropDirectorySizeBefore);
		
        FileFilter ageFileFilter = new AgeFileFilter(cutoffDate, true);
        File[] filesToDelete = fileDropDirectory.listFiles(ageFileFilter);
        
        //Delete each file but and check for success 
        for (File f:filesToDelete) {
        	boolean deleted = f.delete();
        	if (!deleted) {
				logger.warn("File {} could not be deleted.", f.getPath());
        	}
        }
        
        logger.info("Deletion of files has completed.");
		long fileDropDirectorySizeAfter = FileUtils.sizeOfDirectory(fileDropDirectory);
		logger.info("File drop directory size after cleanup: {}. Freed {} bytes: ", fileDropDirectorySizeAfter, fileDropDirectorySizeBefore - fileDropDirectorySizeAfter);
	}
	
}
