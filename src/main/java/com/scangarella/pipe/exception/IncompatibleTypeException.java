package com.scangarella.pipe.exception;

/**
 * This error means that the type passed is not compatible with the object it was passed to.
 */
public class IncompatibleTypeException extends Error {
    /**
     * Creates a new IncompatibleTypeException without a message.
     */
    public IncompatibleTypeException() {
        super();
    }

    /**
     * Creates a new IncompatibleTypeException with a message
     * @param s The message of the exception.
     */
    public IncompatibleTypeException(String s) {
        super(s);
    }
}
