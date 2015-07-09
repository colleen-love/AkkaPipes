package com.collin.pipe.stereotype;

import akka.actor.ActorRef;
import com.sun.corba.se.impl.io.TypeMismatchException;

import javax.xml.bind.TypeConstraintException;
import java.util.List;

/**
 * A basic pipe. Data of type I is operated on by the 'ingest' method.
 * The resultant data of type O is then forwarded to the downstream pipes.
 * This pipe is to be overridden to transform data in some way.
 * While type parameters are never handled by the programmer, its important that they are entered correctly
 * when extending this object as that information is used when building these pipe objects.
 * For example, MyPipe extends PipeRep<String, String>.
 * @param <I> The type of object to be received.
 * @param <O> The type of object to be sent.
 */
public abstract class Pipe<I, O> extends AbstractPipe<I, O> {
    /**
     * Creates a new pipe instance. Downstream pipes must be provided (although the list may be empty).
     * @param downstreamPipes The pipes to which the resultant message should be routed.
     */
    public Pipe(List<ActorRef> downstreamPipes) {
        super(downstreamPipes);
    }

    /**
     *  Sends the outbound O object to the downstream pipes.
     * @param outbound The outbound message to be sent.
     */
    protected final void sendMessageDownstream(O outbound) {
        for (ActorRef downstreamPipe : this.downstreamPipes) {
            downstreamPipe.tell(outbound, this.getSelf());
        }
    }

    /**
     * Additional logic to ensure that the pipe is behaving correctly.
     * @param inbound The object received.
     * @param outbound The object to send.
     * @return Whether or not the pipe conforms to it's stereotype.
     */
    protected Boolean additionalLogic(I inbound, O outbound) {
        return true;
    }
    /**
     * This message is called receipt of data of type I (from upstream pipes).
     * It ingests the message to produce an object of type O and sends it downstream.
     * Null handling of messages occurs here, there is no need for it to be implemented
     * in the 'ingest' menthod.
     * @param message The object that is received for processing.
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void onReceive(Object message) throws Exception {
        if (message != null) {
            I inbound = (I) message;
            O outbound = ingest(inbound);
            if (additionalLogic(inbound, outbound)) {
                sendMessageDownstream(outbound);
            } else {
                throw new TypeMismatchException("PipeRep doesn't conform to stereotype.");
            }
        }
    }

    /**
     * The method to be overriden by other pipes. It will take in data of type I
     * and transofrm it to type O.
     * @param i The data received by upstream pipes.
     * @return The transformed data to send to downstream pipes.
     */
    public abstract O ingest(I i);
}
