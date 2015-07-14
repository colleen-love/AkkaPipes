package com.scangarella.pipe.stereotype;

/**
 * This pipe stereotype takes in an object and decides whether or not to send the object downstream.
 * If the object is not to be sent, null is to be sent instead.
 * @param <T> The data to enter and exit the pipe.
 */
public abstract class FilterPipe<T> extends Pipe<T, T> { }
