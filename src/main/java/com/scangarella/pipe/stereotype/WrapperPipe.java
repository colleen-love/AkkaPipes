package com.scangarella.pipe.stereotype;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.InitializationMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A pipe to wrap another pipe. The incoming objects are to be specially handled
 * by the wrapper pipe's onReceive method. Messages to the inner pipe(s) can be forwarded
 * by the 'tell' method.
 * Inner pipes will all be of the same type.
 */
public abstract class WrapperPipe extends UntypedActor {

    private List<Class> innerPipes = null;
    private List<ActorRef> downstream = null;
    private ActorRef exception = null;

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
            ref = getContext().actorOf(Props.create(innerPipe));
            ref.tell(new InitializationMessage(innerInnerPipes, downstream, exception), this.getSelf());
        } else {
            ref = getContext().actorOf(Props.create(innerPipe));
            InitializationMessage init = new InitializationMessage(downstream, exception);
            ref.tell(init, this.getSelf());
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
            if (message instanceof InitializationMessage) {
                initializePipe((InitializationMessage) message);
            } else {
                ingest(message);
            }
        }
    }

    private void initializePipe(InitializationMessage message) {
        this.innerPipes = message.getInner();
        this.downstream = message.getDownstream();
        this.exception = message.getException();
        initSystem();
    }

    /**
     * The method to be overridden to handle messages.
     * @param message The message to be handled.
     */
    public abstract void ingest(Object message);

    /**
     * Initializes the system after the pipe's inner pipes have been set.
     */
    protected void initSystem() { }

}

