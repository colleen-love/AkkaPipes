package com.scangarella.pipe.stereotype;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.scangarella.pipe.error.IncompatibleTypeException;
import com.scangarella.pipe.transmission.ErrorMessage;
import com.scangarella.pipe.transmission.InitializationMessage;

import java.util.List;

/**
 * This represents an abstract pipe from which all pipe classes (except the wrapperpipe)
 * are extended. Do not directly extend this class, instead extend one of the other stereotypes.
 * @param <I> The type of object to be received.
 * @param <O> The resulting type of object after the processing.
 */
public abstract class AbstractPipe<I, O> extends UntypedActor {

    protected List<ActorRef> downstreamPipes;
    private ActorRef exceptionHandler;
    /**
     * This message is called receipt of data of type I (from upstream pipes).
     * It ingests the message to produce an object of type O and sends it downstream.
     * Null handling of messages occurs here, there is no need for it to be implemented
     * in the 'ingest' method.
     * @param message The object that is received for processing.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void onReceive(Object message) {
        if (message != null) {
            if(message instanceof  InitializationMessage) {
                initializePipe((InitializationMessage)message);
            } else {
                I inbound = (I) message;
                O outbound = ingest(inbound);
                if (additionalLogic(inbound, outbound)) {
                    send(outbound);
                } else {
                    throw new IncompatibleTypeException("Pipe doesn't conform to stereotype.");
                }
            }
        }
    }
    private void initializePipe(InitializationMessage message) {
        this.downstreamPipes = message.getDownstream();
        this.exceptionHandler = message.getException();
    }
    /**
     * The method to be overridden by other pipes. It will take in data of type I
     * and transform it to type O.
     * @param i The data received by upstream pipes.
     * @return The transformed data to send to downstream pipes.
     */
    public abstract O ingest(I i);

    /**
     * This method is used to send data to the next recipient.
     * @param outbound The data to be sent downstream.
     */
    protected abstract void send(O outbound);

    /**
     * Additional logic to ensure that the pipe is behaving correctly.
     * @param inbound The object received.
     * @param outbound The object to send.
     * @return Whether or not the pipe conforms to it's stereotype.
     */
    protected Boolean additionalLogic(I inbound, O outbound) {
        return true;
    }

    protected void reportError(ErrorMessage errorMessage) {
        this.exceptionHandler.tell(errorMessage, this.getSelf());
    }
}
