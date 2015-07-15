package com.scangarella.pipe.stereotype;

import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.ErrorMessage;
import com.scangarella.pipe.transmission.InitializationMessage;

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
        if (message != null) {
            if (message instanceof InitializationMessage) {
                initializePipe((InitializationMessage) message);
            } else {
                onError((ErrorMessage) message);
            }
        }
    }

    private void initializePipe(InitializationMessage message) {

    }

    /**
     * Process the received error message
     * @param message The message to process.
     */
    public abstract void onError(ErrorMessage message);
}
