package com.collin.pipe.transmission;

public class Message<I> {

    private String id;
    private I i;
    public Message(String id, I i) {
        this.id = id;
        this.i = i;
    }
    public String getId() {
        return this.id;
    }
    public I getInfo() {
        return this.i;
    }

}
