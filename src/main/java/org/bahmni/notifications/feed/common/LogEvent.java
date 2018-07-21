package org.bahmni.notifications.feed.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Category;

public class LogEvent {
    /**
     * Write to the log file (type error)
     * @param className the class name
     * @param methodName the method name
     * @param errorMessage the error message
     */
    public static void logError(String className, String methodName, String errorMessage) {
        getLog().error("Class: " + className + ", Method: " + methodName + ", Error: " + errorMessage);
    }

    /**
     * Write to the log file (type error)
     * @param className the class name
     * @param methodName the method name
     * @param throwable -- exception which will be used to generate the stack trace
     */
    public static void logErrorStack(String className, String methodName, Throwable throwable) {
        logError(className, methodName, throwable.toString());
        getLog().error("Class: " + className + ", Method: " + methodName , throwable);
    }
    /**
     * Write to the log file (type debug)
     * @param className the class name
     * @param methodName the method name
     * @param debugMessage the debug message
     */
    public static void logDebug(String className, String methodName, String debugMessage) {
        getLog().debug("Class: " + className + ", Method: " + methodName + ", Debug: " + debugMessage);
    }

    /**
     * Write to the log file (type info)
     * @param className the class name
     * @param methodName the method name
     * @param infoMessage the info message
     */
    public static void logInfo(String className, String methodName, String infoMessage) {
        getLog().info("Class: " + className + ", Method: " + methodName + ", Info: " + infoMessage);
    }

    /**
     * Write to the log file (type warning)
     * @param className the class name
     * @param methodName the method name
     * @param warnMessage the warning message
     */
    public static void logWarn(String className, String methodName, String warnMessage) {
        getLog().warn("Class: " + className + ", Method: " + methodName + ", Warning:" + warnMessage);
    }

    /**
     * Write to the log file (type fatal)
     * @param className the class name
     * @param methodName the method name
     * @param fatalMessage the fatal message
     */
    public static void logFatal(String className, String methodName, String fatalMessage) {
        getLog().fatal("Class: " + className + ", Method: " + methodName + ", Fatal:" + fatalMessage);
    }

    public static Log getLog(Class className) {
        Log log = LogFactory.getLog(className);
        return log;
    }

    private static Category getLog() {
        return Category.getInstance(LogEvent.class);
    }
}
