package com.scangarella.pipe.stereotype;

/**
 * This pipe stereotype takes in an object and decides whether or not to send the object downstream.
 * If the object is not to be sent, null is to be sent instead.
 * @param <T> The data to enter and exit the pipe.
 */
public abstract class FilterPipe<T> extends Pipe<T, T> {

    /**
     * Checks to ensure that the pipe conforms to the filter stereotype.
     * @param inbound The object received.
     * @param outbound The object to send.
     * @return True if outbound is null. True if inbound equals outbound. False otherwise.
     */
    @Override
    protected Boolean additionalLogic(T inbound, T outbound) {
        return outbound == null || inbound == outbound;
    }
}
