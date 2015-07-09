package com.collin.pipe.stereotype;

import akka.actor.ActorRef;
import com.sun.corba.se.impl.io.TypeMismatchException;

import java.util.List;

/**
 * A pipe to wrap another pipe. The incoming objects are to be specially handled
 * by the wrapper pipe's onReceive method. Messages to the inner pipe(s) can be forwarded
 * by the 'tell' method.
 * Inner pipes will all be of the same type.
 * @param <I>
 */
public abstract class WrapperPipe<I, O> extends AbstractPipe<I, O> {
    /**
     * The class that all inner pipes will be.
     */
    protected Class innerPipe;

    /**
     * Creates a new pipe wrapper. Downstream pipes must be provided (although the list may be empty).
     * @param inner The type of objects that this pipe will contain.
     * @param downstreamPipes The pipes to which the resultant message should be routed.    * @param inner
     */
    public WrapperPipe(Class inner, List<ActorRef> downstreamPipes) {
        super(downstreamPipes);
        if (AbstractPipe.class.isAssignableFrom(inner)) {
            this.innerPipe = inner;
        } else {
            throw new TypeMismatchException();
        }
    }
}
