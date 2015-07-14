package com.scangarella.pipe.stereotype;

/**
 * This pipe takes in an object of type T and sends it back out unchanged.
 * Side effects occur, however without impacting the object itself.
 * @param <T> the type of data to enter and exit the pipe.
 */
public abstract class SideEffectPipe<T> extends Pipe<T, T> { }
