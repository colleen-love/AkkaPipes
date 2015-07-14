package com.scangarella.pipe.stereotype;

import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.ErrorMessage;

public abstract class ErrorHandler extends UntypedActor{
    @Override
    @SuppressWarnings("unchecked")
    public final void onReceive(Object message) throws Exception {
        onError((ErrorMessage) message);
    }
    public abstract void onError(ErrorMessage message);
}
