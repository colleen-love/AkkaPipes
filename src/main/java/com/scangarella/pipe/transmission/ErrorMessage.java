package com.scangarella.pipe.transmission;

import com.scangarella.pipe.error.IncompatibleTypeException;

public class ErrorMessage {
    private Class errorType;
    private String message;
    @SuppressWarnings("unchecked")
    public ErrorMessage(Class errorType, String message) {
        if (!errorType.isAssignableFrom(Error.class)){
            throw new IncompatibleTypeException();
        } else {
            this.message = message;
            this.errorType = errorType;
        }
    }
    public String getMessage() {
        return this.message;
    }
    public Class getErrorType() {
        return this.errorType;
    }
}
