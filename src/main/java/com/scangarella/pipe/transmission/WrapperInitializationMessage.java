package com.scangarella.pipe.transmission;

import akka.actor.ActorRef;

import java.util.List;

public class WrapperInitializationMessage extends InitializationMessage {
    private List<Class> inner;
    public WrapperInitializationMessage(List<Class> inner) {
        this.inner = inner;
    }
    public WrapperInitializationMessage(List<Class> inner, List<ActorRef> downstream, ActorRef exception) {
        super(downstream, exception);
        this.inner = inner;
    }
    public List<Class> getInner() {
        return this.inner;
    }
}
