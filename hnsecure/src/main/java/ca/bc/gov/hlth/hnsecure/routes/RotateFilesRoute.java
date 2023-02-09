package ca.bc.gov.hlth.hnsecure.routes;

import static ca.bc.gov.hlth.hnsecure.properties.ApplicationProperty.FILE_DROPS_ROTATION_CRON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.hlth.hnsecure.filedrops.RotateFilesProcessor;
import ca.bc.gov.hlth.hnsecure.properties.ApplicationProperties;

/**
 * Route to trigger processing of log files for cleanup. 
 *
 */
public class RotateFilesRoute extends BaseRoute {

	private static Logger logger = LoggerFactory.getLogger(RotateFilesProcessor.class);

	private static final ApplicationProperties properties = ApplicationProperties.getInstance();

	@Override
	public void configure() throws Exception {

		String cronSchedule = properties.getValue(FILE_DROPS_ROTATION_CRON);
		logger.info("CRON Schedule: {}", cronSchedule);
		final String rotationSchedule = String.format("quartz://rotateFiles?cron=%s", cronSchedule);

		from(rotationSchedule).routeId("rotate-files-route")
			.log("Processing file rotation")
			.to("direct:processFileRotation").id("ToProcessFileRotation");
		
		from("direct:processFileRotation").id("ProcessFileRotation")
		.process(new RotateFilesProcessor());
	}

}
