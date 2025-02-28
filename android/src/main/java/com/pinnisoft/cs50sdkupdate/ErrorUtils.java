package com.pinnisoft.cs50sdkupdate;

import android.util.Log;

import io.flutter.plugin.common.MethodChannel.Result;

/**
 * Utility class for standardized error handling across the plugin
 */
public class ErrorUtils {
    private static final String TAG = "ErrorUtils";

    // Error codes
    public static final String ERROR_IO = "IO_ERROR";
    public static final String ERROR_HARDWARE = "HARDWARE_ERROR";
    public static final String ERROR_CONFIGURATION = "CONFIG_ERROR";
    public static final String ERROR_PERMISSION = "PERMISSION_ERROR";
    public static final String ERROR_TIMEOUT = "TIMEOUT_ERROR";
    public static final String ERROR_INVALID_ARGUMENT = "INVALID_ARGUMENT";
    public static final String ERROR_NOT_SUPPORTED = "NOT_SUPPORTED";
    public static final String ERROR_RESOURCE_BUSY = "RESOURCE_BUSY";
    public static final String ERROR_NOT_FOUND = "NOT_FOUND";
    public static final String ERROR_UNEXPECTED = "UNEXPECTED_ERROR";

    /**
     * Handle an exception and return appropriate error to Flutter
     *
     * @param method The method that was being called when the exception occurred
     * @param exception The exception that was thrown
     * @param result The Flutter result to send the error to
     */
    public static void handleException(String method, Exception exception, Result result) {
        Log.e(TAG, "Error in method " + method, exception);

        String errorCode;
        String errorMessage = exception.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "An error occurred in " + method;
        }

        // Determine error type from exception class
        if (exception instanceof IllegalArgumentException) {
            errorCode = ERROR_INVALID_ARGUMENT;
        } else if (exception instanceof UnsupportedOperationException) {
            errorCode = ERROR_NOT_SUPPORTED;
        } else if (exception instanceof SecurityException) {
            errorCode = ERROR_PERMISSION;
        } else if (exception instanceof java.io.IOException) {
            errorCode = ERROR_IO;
        } else if (exception instanceof java.util.concurrent.TimeoutException) {
            errorCode = ERROR_TIMEOUT;
        } else if (exception instanceof java.io.FileNotFoundException) {
            errorCode = ERROR_NOT_FOUND;
        } else {
            errorCode = ERROR_UNEXPECTED;
        }

        String details = getExceptionDetails(exception);
        result.error(errorCode, errorMessage, details);
    }

    /**
     * Extract formatted details from an exception
     */
    private static String getExceptionDetails(Exception e) {
        StringBuilder details = new StringBuilder();
        details.append("Exception type: ").append(e.getClass().getName()).append("\n");

        if (e.getCause() != null) {
            details.append("Cause: ").append(e.getCause().toString()).append("\n");
        }

        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            details.append("Stack trace:\n");
            // Include only first 5 stack trace elements to keep the output manageable
            int linesToInclude = Math.min(5, stackTrace.length);
            for (int i = 0; i < linesToInclude; i++) {
                details.append("    ").append(stackTrace[i].toString()).append("\n");
            }
            if (stackTrace.length > linesToInclude) {
                details.append("    ... (").append(stackTrace.length - linesToInclude).append(" more)");
            }
        }

        return details.toString();
    }

    /**
     * Create standard error for invalid arguments
     */
    public static void invalidArgument(Result result, String message) {
        Log.e(TAG, "Invalid argument: " + message);
        result.error(ERROR_INVALID_ARGUMENT, message, null);
    }

    /**
     * Create standard error for hardware issues
     */
    public static void hardwareError(Result result, String message, int errorCode) {
        Log.e(TAG, "Hardware error: " + message + " (code: " + errorCode + ")");
        result.error(ERROR_HARDWARE, message, "Error code: " + errorCode);
    }

    /**
     * Create standard error for file not found
     */
    public static void fileNotFound(Result result, String path) {
        Log.e(TAG, "File not found: " + path);
        result.error(ERROR_NOT_FOUND, "File not found: " + path, null);
    }
}