package com.scangarella.pipe.transmission;


/**
 * An error message including the errorType and the string message.
 */
public class ExceptionMessage {
    private Exception exception;

    public ExceptionMessage(Exception e) {
        this.exception = e;
    }

    public Exception getException() {
        return this.exception;
    }
}
