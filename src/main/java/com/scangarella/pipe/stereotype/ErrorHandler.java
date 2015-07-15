package com.scangarella.pipe.stereotype;

import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.ErrorMessage;

/**
 * An error handler is used to process error messages.
 */
public abstract class ErrorHandler extends UntypedActor{
    /**
     * Receives a message from a pipe and processes the message.
     * @param message The received message.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void onReceive(Object message) {
        onError((ErrorMessage) message);
    }

    /**
     * Process the received error message
     * @param message The message to process.
     */
    public abstract void onError(ErrorMessage message);
}
