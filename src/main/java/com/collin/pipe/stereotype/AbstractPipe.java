package com.collin.pipe.stereotype;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.util.List;

/**
 * An Abstract pipe. This class is the foundation for an object which takes in an object,
 * does an operation on the object, and sends the resultant object out.
 * Do not extend this pipe. Instead, extend the other stereotypes.
 * The implementation of receiving and sending information is left to subclasses.
 * @param <I> The type of object that the pipe will receive and process.
 * @param <O> The type of object that will result from the processing.
 */
public abstract class AbstractPipe<I, O> extends UntypedActor {
    /**
     * A list of the pipes to which to route the resultant data.
     */
    protected final List<ActorRef> downstreamPipes;

    /**
     * Creates a new pipe instance. Downstream pipes must be provided (although the list may be empty).
     * @param downstreamPipes The pipes to which the resultant message should be routed.
     */
    public AbstractPipe(List<ActorRef> downstreamPipes) {
        this.downstreamPipes = downstreamPipes;
    }
}
