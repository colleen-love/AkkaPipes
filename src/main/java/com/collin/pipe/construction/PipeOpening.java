package com.collin.pipe.construction;

import akka.actor.ActorRef;

/**
 * A pipe opening into which objects can be put.
 * @param <I> The type of objects to be put into the pipe.
 */
public final class PipeOpening<I> {

    private ActorRef pipeline;

    /**
     * Creates a new PipeOpening for the specified pipeline..
     * @param pipeline The pipeline that the opening interacts with.
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
