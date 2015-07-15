package com.scangarella.pipe.stereotype;

/**
 * A pipe which transforms one object into one or more objects.
 * Data of type I is operated on by the ingest method; an Iterable of type O results.
 * Each object of type O is sent to the downstream pipes.
 * @param <I> The type of object to be received.
 * @param <O> The type of object to be sent.
 */
public abstract class MultiPipe<I, O> extends AbstractPipe<I, Iterable<O>> {

    /**
     * Sends the outbound O objects to the downstream pipes.
     * @param outbound a series of O objects to be sent downstream.
     */
    @Override
    protected final void send(Iterable<O> outbound) {
        if (outbound != null && this.downstreamPipes != null) {
            for (O o : outbound) {
                if (o != null) {
                    this.downstreamPipes.forEach(pipe -> pipe.tell(o, this.getSelf()));
                }
            }
        }
    }
}
