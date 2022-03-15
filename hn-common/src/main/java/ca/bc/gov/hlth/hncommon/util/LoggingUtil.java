package ca.bc.gov.hlth.hncommon.util;

import java.util.Optional;

/**
 * Utility class that contains methods related to logging.
 * 
 */
public final class LoggingUtil {
	
	private static final String NO_UTIL_INSTANCE = "This is a utility class and cannot be instantiated";

	private LoggingUtil() {
		throw new UnsupportedOperationException(NO_UTIL_INSTANCE);
	}

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
