package com.scangarella.pipe.transmission;

import com.scangarella.pipe.error.IncompatibleTypeException;

/**
 * An error message including the errorType and the string message.
 */
public class ErrorMessage {
    private Class errorType;
    private String message;

    /**
     * Creates a new message
     * @param errorType The type of exception.
     * @param message The message associated with this exception.
     */
    @SuppressWarnings("unchecked")
    public ErrorMessage(Class errorType, String message) {
        if (!errorType.isAssignableFrom(Error.class)){
            throw new IncompatibleTypeException();
        } else {
            this.message = message;
            this.errorType = errorType;
        }
    }

    /**
     * Gets the message associated with this exception.
     * @return the string message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the type of exception associated with the message.
     * @return The type of exception.
     */
    public Class getErrorType() {
        return this.errorType;
    }
}
