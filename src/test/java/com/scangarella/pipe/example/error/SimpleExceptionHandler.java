package com.scangarella.pipe.example.error;

import com.scangarella.pipe.stereotype.ExceptionHandler;
import com.scangarella.pipe.transmission.ExceptionMessage;

public class SimpleExceptionHandler extends ExceptionHandler {

    @Override
    public void onException(ExceptionMessage exceptionMessage) {
        System.out.println(exceptionMessage.getException().getMessage());
    }
}
