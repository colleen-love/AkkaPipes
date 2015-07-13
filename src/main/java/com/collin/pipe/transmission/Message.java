package com.collin.pipe.transmission;

/**
 * The message sent between pipeline and pipes
 * @param <I> The data contained in the message
 */
public final class Message<I> {

    private String id;
    private I i;

    /**
     * Creates a new message
     * @param id The identifier of the message
     * @param i the information contained within the message.
     */
    public Message(String id, I i) {
        this.id = id;
        this.i = i;
    }

    /**
     * Returns the message's identifier.
     * @return the message's identifier.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the message's content.
     * @return the content of the message.
     */
    public I getInfo() {
        return this.i;
    }

}
