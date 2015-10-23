package com.scangarella.pipe.stereotype;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.ExceptionMessage;
import com.scangarella.pipe.transmission.InitializationMessage;
import com.scangarella.pipe.transmission.StopMessage;

import java.util.List;

/**
 * This represents an abstract pipe from which all pipe classes (except the wrapperpipe)
 * are extended. Do not directly extend this class, instead extend one of the other stereotypes.
 * @param <I> The type of object to be received.
 * @param <O> The resulting type of object after the processing.
 */
public abstract class AbstractPipe<I, O> extends UntypedActor {

    /**
     * The downstream pipes of this pipe
     */
    protected List<ActorRef> downstreamPipes;
    private ActorRef exceptionHandler;
    private Integer upstreamPipeCount;
    private Integer receivedStopMessages = 0;
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
            } else if (message instanceof StopMessage) {
                receivedStopMessages++;
                if (receivedStopMessages.equals(upstreamPipeCount)) {
                    downstreamPipes.forEach(x -> x.tell(new StopMessage(), this.getSelf()));
                    this.getSelf().tell(PoisonPill.getInstance(), this.getSelf());
                }
            }
            else{
                I inbound = (I) message;
                O outbound = ingest(inbound);
                send(outbound);
            }
        }
    }

    private void initializePipe(InitializationMessage message) {
        this.downstreamPipes = message.getDownstream();
        this.exceptionHandler = message.getException();
        this.upstreamPipeCount = message.getUpstreamCount();
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
     * Reports an error to this pipe's error handler, if it exists.
     * @param errorMessage The error message to send to the error handler.
     */
    protected void reportError(ExceptionMessage errorMessage) {
        if (this.exceptionHandler != null) {
            this.exceptionHandler.tell(errorMessage, this.getSelf());
        }
    }
}
