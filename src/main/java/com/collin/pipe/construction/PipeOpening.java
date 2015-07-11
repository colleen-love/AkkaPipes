package com.collin.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * A pipe opening into which objects can be put.
 * @param <I> The type of objects to be put into the pipe.
 */
public final class PipeOpening<I> {
    /**
     * The first pipe in the pipeline.
     */
    private ActorRef pipeline;

    /**
     * Creates a new PipeOpening with the specified first pipe.
     * @param pipeline The first pipe in the pipeline.
     */
    public PipeOpening(ActorRef pipeline){
        this.pipeline = pipeline;
    }

    /**
     * Puts an object of type I into the pipeline.
     * @param i the object to be put into the pipeline.
     */
    public void put(I i) {
        pipeline.tell(i, pipeline);
    }
}
