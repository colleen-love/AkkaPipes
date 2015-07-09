package com.collin.pipe.stereotype;

import akka.actor.ActorRef;

import java.util.List;

/**
 * This pipe takes in an object of type T and sends it back out unchanged.
 * Side effects occur, however without impacting the object itself.
 * @param <T> the type of data to enter and exit the pipe.
 */
public abstract class SideEffectPipe<T> extends Pipe<T, T> {
    /**
     * Creates a new pipe instance. Downstream pipes must be provided (although the list may be empty).
     * @param downstreamPipes The pipes to which the resultant message should be routed.
     */
    public SideEffectPipe(List<ActorRef> downstreamPipes) {
        super(downstreamPipes);
    }

    /**
     * Checks to ensure that the incoming object is the same as the outgoing object.
     * @param inbound The object received.
     * @param outbound The object to send.
     * @return True if inbound equals outbound, false otherwise.
     */
    @Override
    protected Boolean additionalLogic(T inbound, T outbound) {
        return inbound == outbound;
    }
}
