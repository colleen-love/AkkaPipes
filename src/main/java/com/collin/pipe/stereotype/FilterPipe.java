package com.collin.pipe.stereotype;

import akka.actor.ActorRef;

import java.util.List;

/**
 * This pipe stereotype takes in an object and decides whether or not to send the object downstream.
 * If the object is not to be sent, null is to be sent instead.
 * @param <T> The data to enter and exit the pipe.
 */
public abstract class FilterPipe<T> extends Pipe<T, T> {
    /**
     * Creates a new pipe instance. Downstream pipes must be provided (although the list may be empty).
     * @param downstreamPipes The pipes to which the resultant message should be routed.
     */
    public FilterPipe(List<ActorRef> downstreamPipes) {
        super(downstreamPipes);
    }

    /**
     * Checks to ensure that the pipe conforms to the filter stereotype.
     * @param inbound The object received.
     * @param outbound The object to send.
     * @return True if outbound is null. True if inbound equals outbound. False otherwise.
     */
    @Override
    protected Boolean additionalLogic(T inbound, T outbound) {
        return outbound == null || inbound == outbound;
    }
}
