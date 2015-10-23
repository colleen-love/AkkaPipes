package com.scangarella.pipe.stereotype;

import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.ExceptionMessage;
import com.scangarella.pipe.transmission.InitializationMessage;

/**
 * An error handler is used to process error messages.
 */
public abstract class ExceptionHandler extends UntypedActor{
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
                onException((ExceptionMessage) message);
            }
        }
    }

    private void initializePipe(InitializationMessage message) {

    }

    /**
     * Process the received error message
     * @param message The message to process.
     */
    public abstract void onException(ExceptionMessage message);
}
