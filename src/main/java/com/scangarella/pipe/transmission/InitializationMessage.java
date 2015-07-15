package com.scangarella.pipe.transmission;

import akka.actor.ActorRef;

import java.util.List;

/**
 * An initialization message. This message can contain the following types of initialization:
 * Inner classes, downstream pipes, and exception handler: used for wrapper pipes.
 * Downstream pipe and exception handler: used for pipes.
 * Inner classes: used for exception handler's wrappers.
 */
public class InitializationMessage {
    private List<ActorRef> downstream = null;
    private ActorRef exception = null;
    private List<Class> inner;

    /**
     * Creates a new InitializationMessage meant for an exception handler's wrapper.
     * @param inner The inner classes.
     */
    public InitializationMessage(List<Class> inner) {
        this.inner = inner;
    }

    /**
     * Creates a new InitializationMessage meant for a pipe.
     * @param downstream The pipe's downstream pipes.
     * @param exception The exception handler for any issues.
     */
    public InitializationMessage(List<ActorRef> downstream, ActorRef exception){
        this.downstream = downstream;
        this.exception = exception;
    }

    /**
     * Creates a new InitializationMessage meant for a wrapper.
     * @param inner The inner classes for this pipe.
     * @param downstream The innermost pipe's downstream pipes.
     * @param exception The innermost pipe's exception handler.
     */
    public InitializationMessage(List<Class> inner, List<ActorRef> downstream, ActorRef exception) {
        this.downstream = downstream;
        this.exception = exception;
        this.inner = inner;
    }

    /**
     * Gets the message's inner classes.
     * @return The inner classes.
     */
    public List<Class> getInner() {
        return this.inner;
    }

    /**
     * Gets the message's downstream pipes.
     * @return The downstream pipes.
     */
    public List<ActorRef> getDownstream() {
        return this.downstream;
    }

    /**
     * Gets the exception handler.
     * @return The exception handler.
     */
    public ActorRef getException() {
        return this.exception;
    }
}
