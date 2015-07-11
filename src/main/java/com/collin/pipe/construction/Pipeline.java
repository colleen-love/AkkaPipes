package com.collin.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.collin.pipe.transmission.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * This pipeline directs messages to the pipes from the root to the downstream pipes.
 */
public final class Pipeline extends UntypedActor {

    private ActorRef end = null;
    Map<String, Schematic.Pipe> map = new HashMap<>();
    Schematic.Pipe root;

    /**
     * Creates a new pipeline based on the schematic.
     * @param schematic The schematic on which to base the pipeline
     */
    public Pipeline(Schematic schematic) {
        this(schematic, null);
    }

    /**
     * Creates a new ended pipeline based on the schematic.
     * @param schematic The schematic on which to base the pipeline.
     * @param end The Actor to which the completed objects are sent.
     */
    public Pipeline(Schematic schematic, ActorRef end) {
        this.root = schematic.getRoot();
        schematic.allPipes().forEach(pipe -> map.put(pipe.getId(), pipe));
        this.end = end;
        schematic.disable();
    }

    /**
     * This is called when a message is to be routed. The pipeline then directs the
     * message to the appropriate downstream pipes.
     * @param message The message to be sent.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onReceive(Object message) {
        if (message instanceof Message) {
            Message receivedInfo = (Message) message;
            Schematic.Pipe p = map.get(receivedInfo.getId());
            if (p.hasChildren()) {
                p.getChildren().forEach(child -> {
                    Message<Object> sentInfo = new Message<>(child.getId(), receivedInfo.getInfo());
                    child.getActorRef().tell(sentInfo, getSelf());
                });
            } else {
                if (this.end != null) {
                    this.end.tell(receivedInfo.getInfo(), getSelf());
                }
            }

        } else {
            Message info = new Message(this.root.getId(), message);
            this.root.getActorRef().tell(info, getSelf());
        }
    }
}
