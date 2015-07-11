package com.collin.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.collin.pipe.transmission.Message;

import java.util.HashMap;
import java.util.Map;

public final class Pipeline extends UntypedActor {

    private ActorRef end = null;
    Map<String, Schematic.Pipe> map = new HashMap<>();
    Schematic.Pipe root;

    public Pipeline(Schematic schematic) {
        this(schematic, null);
    }
    public Pipeline(Schematic schematic, ActorRef end) {
        this.root = schematic.getRoot();
        schematic.allPipes().forEach(pipe -> {
            map.put(pipe.getId(), pipe);
        });
        this.end = end;
        schematic.disable();
    }
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Message) {
            Message receivedInfo = (Message) message;
            Schematic.Pipe p = map.get(receivedInfo.getId());
            if (p.hasChildren()) {
                p.getChildren().forEach(child -> {
                    Message sentInfo = new Message(child.getId(), receivedInfo.getInfo());
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
