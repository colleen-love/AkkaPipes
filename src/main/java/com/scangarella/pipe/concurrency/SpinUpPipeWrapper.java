package com.scangarella.pipe.concurrency;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import com.scangarella.pipe.stereotype.WrapperPipe;

import java.util.List;

/**
 * Spins up a new inner pipe every time there is a message to ingest.
 */
public class SpinUpPipeWrapper extends WrapperPipe {
    /**
     * Creates a new inner pipe and forwards along the message.
     * After the inner pipe finishes ingesting the message, it is discarded.
     * @param message The message to be handled.
     */
    @Override
    public void ingest(Object message) {
        ActorRef innerPipe = buildInnerPipe();
        innerPipe.tell(message, getSender());
        innerPipe.tell(PoisonPill.getInstance(), getSender());
    }
}
