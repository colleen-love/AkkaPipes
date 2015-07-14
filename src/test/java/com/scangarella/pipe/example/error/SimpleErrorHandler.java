package com.scangarella.pipe.example.error;

import com.scangarella.pipe.stereotype.ErrorHandler;
import com.scangarella.pipe.transmission.ErrorMessage;

public class SimpleErrorHandler extends ErrorHandler {

    @Override
    public void onError(ErrorMessage message) {
        System.out.println(message.getErrorType() + ": " + message.getMessage());
    }
}
