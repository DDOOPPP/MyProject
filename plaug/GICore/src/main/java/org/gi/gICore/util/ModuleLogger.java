package org.gi.gICore.util;

import org.gi.gICore.CoreLogger;

public class ModuleLogger {
    private final CoreLogger coreLogger;
    private final String moduleName;

    public ModuleLogger(CoreLogger coreLogger, String moduleName) {
        this.coreLogger = coreLogger;
        this.moduleName = moduleName;
    }

    private String formatMessage(String message) {
        return "[" + moduleName + "] " + message;
    }

    public void info(String message) {
        coreLogger.info(formatMessage(message));
    }

    public void info(String format, Object... args) {
        coreLogger.info(formatMessage(String.format(format, args)));
    }

    public void warn(String message) {
        coreLogger.warn(formatMessage(message));
    }

    public void warn(String format, Object... args) {
        coreLogger.warn(formatMessage(String.format(format, args)));
    }

    public void warn(String message, Throwable throwable) {
        coreLogger.warn(formatMessage(message), throwable);
    }

    public void error(String message) {
        coreLogger.error(formatMessage(message));
    }

    public void error(String format, Object... args) {
        coreLogger.error(formatMessage(String.format(format, args)));
    }

    public void error(String message, Throwable throwable) {
        coreLogger.error(formatMessage(message), throwable);
    }

    public void debug(String message) {
        coreLogger.debug(formatMessage(message));
    }

    public void debug(String format, Object... args) {
        coreLogger.debug(formatMessage(String.format(format, args)));
    }
}
