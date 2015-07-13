package com.scangarella.pipe.stereotype;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.sun.corba.se.impl.io.TypeMismatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * A pipe to wrap another pipe. The incoming objects are to be specially handled
 * by the wrapper pipe's onReceive method. Messages to the inner pipe(s) can be forwarded
 * by the 'tell' method.
 * Inner pipes will all be of the same type.
 * @param <I>
 */
public abstract class WrapperPipe extends UntypedActor {

    private List<Class> innerPipes;

    /**
     * Creates a new pipe wrapper. Downstream pipes must be provided (although the list may be empty).
     * @param innerPipes The type of objects that this pipe will contain.
     */
    public WrapperPipe(List<Class> innerPipes) {
        innerPipes.forEach(clazz -> {
            if (!AbstractPipe.class.isAssignableFrom(clazz)) {
                throw new TypeMismatchException();
            }
        });
        this.innerPipes = innerPipes;
    }

    /**
     * Builds an instance of the wrapper's inner pipe.
     * @return The actor ref used to reference the inner pipe.
     */
    protected ActorRef buildInnerPipe() {
        ActorRef ref;
        Class innerPipe = this.innerPipes.get(this.innerPipes.size() - 1);
        if (innerPipes.size() > 1) {
            List<Class> innerInnerPipes = new ArrayList<>(this.innerPipes);
            innerInnerPipes.remove(innerInnerPipes.size() - 1);
            ref = getContext().actorOf(Props.create(innerPipe, innerInnerPipes));
        } else {
            ref = getContext().actorOf(Props.create(innerPipe));
        }
        return ref;
    }

    /**
     * Routes the received message to the pipe's ingest method.
     * @param message Message to be ingested.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void onReceive(Object message) {
        if (message != null) {
            ingest(message);
        }
    }

    /**
     * The method to be overridden to handle messages.
     * @param message The message to be handled.
     */
    public abstract void ingest(Object message);

}

