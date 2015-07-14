package com.scangarella.pipe.transmission;

import akka.actor.ActorRef;

import java.util.List;

public class InitializationMessage {
    private List<ActorRef> downstream = null;
    private ActorRef exception = null;
    public InitializationMessage() { }
    public InitializationMessage(List<ActorRef> downstream, ActorRef exception){
        this.downstream = downstream;
        this.exception = exception;
    }
    public List<ActorRef> getDownstream() {
        return this.downstream;
    }
    public ActorRef getException() {
        return this.exception;
    }
    public Boolean isBlank() {
        return this.downstream == null && this.exception == null;
    }
}
