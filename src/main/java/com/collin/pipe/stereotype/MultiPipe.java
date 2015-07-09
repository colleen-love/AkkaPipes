package com.collin.pipe.stereotype;

import akka.actor.ActorRef;

import java.util.List;

/**
 * A pipe which transforms one object into one or more objects.
 * Data of type I is operated on by the ingest method; an Iterable of type O results.
 * Each object of type O is sent to the downstream pipes.
 * @param <I> The type of object to be received.
 * @param <O> The type of object to be sent.
 */
public abstract class MultiPipe<I, O> extends AbstractPipe<I, Iterable<O>>{
    /**
     * Creates a new pipe instance. Downstream pipes must be provided (although the list may be empty).
     * @param downstreamPipes The pipes to which the resultant message should be routed.
     */
    public MultiPipe(List<ActorRef> downstreamPipes) {
        super(downstreamPipes);
    }

    /**
     * Sends the outbound O objects to the downstream pipes.
     * @param outbound a series of O objects to be sent downstream.
     */
    protected final void sendMessageDownstream(Iterable<O> outbound) {
        for (ActorRef downstreamPipe : this.downstreamPipes) {
            for(O o : outbound) {
                downstreamPipe.tell(o, this.getSelf());
            }
        }
    }
}
