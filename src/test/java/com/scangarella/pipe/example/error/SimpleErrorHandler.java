package com.scangarella.pipe.example.error;

import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.ErrorMessage;

public class SimpleErrorHandler extends UntypedActor {

    @Override
    public void onReceive(Object message) {
        ErrorMessage em = (ErrorMessage) message;
        System.out.println(em.getErrorType() + ": " + em.getMessage());
    }
}
