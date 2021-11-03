package ca.bc.gov.hlth.hncommon.util;

import java.util.Optional;

/**
 * Utility class that contains methods related to logging.
 * 
 */
public class LoggingUtil {

	/**
     * This method uses StackWalker API to get the names of the current calling method 
     * @return
     */
    public static String getMethodName() {
    	StackWalker walker = StackWalker.getInstance();
    	Optional<String> methodName = walker.walk(frames -> frames
			.limit(2)
			.skip(1) // to get name of caller
			.findFirst()
			.map(StackWalker.StackFrame::getMethodName)
			);
    	return methodName.orElse("Method name Unknnown");
    }

}
