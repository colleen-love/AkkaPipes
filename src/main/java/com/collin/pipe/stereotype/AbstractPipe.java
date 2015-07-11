package com.collin.pipe.stereotype;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.collin.pipe.transmission.Message;
import com.sun.corba.se.impl.io.TypeMismatchException;

public abstract class AbstractPipe<I, O> extends UntypedActor {

    private String id;
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
            Message<I> info = (Message)message;
            I inbound = info.getInfo();
            this.id = info.getId();
            O outbound = ingest(inbound);
            if (additionalLogic(inbound, outbound)) {
                send(outbound);
            } else {
                throw new TypeMismatchException("Pipe doesn't conform to stereotype.");
            }
        }
    }
    protected String getId(){
        return this.id;
    }
    /**
     * The method to be overriden by other pipes. It will take in data of type I
     * and transofrm it to type O.
     * @param i The data received by upstream pipes.
     * @return The transformed data to send to downstream pipes.
     */
    protected abstract O ingest(I i);

    protected abstract void send(O outbound);

    /**
     * Additional logic to ensure that the pipe is behaving correctly.
     * @param inbound The object received.
     * @param outbound The object to send.
     * @return Whether or not the pipe conforms to it's stereotype.
     */
    protected Boolean additionalLogic(I inbound, O outbound) {
        return true;
    }
}
