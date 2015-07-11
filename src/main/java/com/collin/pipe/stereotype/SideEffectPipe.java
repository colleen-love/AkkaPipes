package com.collin.pipe.stereotype;

/**
 * This pipe takes in an object of type T and sends it back out unchanged.
 * Side effects occur, however without impacting the object itself.
 * @param <T> the type of data to enter and exit the pipe.
 */
public abstract class SideEffectPipe<T> extends Pipe<T, T> {

    /**
     * Checks to ensure that the incoming object is the same as the outgoing object.
     * @param inbound The object received.
     * @param outbound The object to send.
     * @return True if inbound equals outbound, false otherwise.
     */
    @Override
    protected Boolean additionalLogic(T inbound, T outbound) {
        return inbound == outbound;
    }
}
