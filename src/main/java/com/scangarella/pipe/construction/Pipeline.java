package com.scangarella.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.scangarella.pipe.transmission.ErrorMessage;
import com.scangarella.pipe.transmission.Message;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ErrorMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This pipeline directs messages to the pipes from the root to the downstream pipes.
 */
public final class Pipeline extends UntypedActor {

    protected ActorRef end = null;
    protected Map<String, PipeRef> map = new HashMap<>();
    protected PipeRef root;

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
        this.root = buildAndMapPipes(schematic.getRoot());
        this.end = end;
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
            PipeRef p = map.get(receivedInfo.getId());
            if (receivedInfo.getInfo() instanceof ErrorMessage) {
                if (p.hasErrorHandler()) {
                    p.getErrorHandler().tell(receivedInfo.getInfo(), getSelf());
                }
            }else {
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
            }
        } else {
            Message info = new Message(this.root.getId(), message);
            this.root.getActorRef().tell(info, getSelf());
        }
    }

    private PipeRef buildAndMapPipes(Schematic.Pipe pipe) {
        PipeRef pipeRef = buildPipe(pipe);
        if (pipe.hasErrorHandler()) {
            pipeRef.setErrorHandler(buildErrorHandler(pipe.getErrorHandler()));
        }
        map.put(pipeRef.getId(), pipeRef);
        for(Schematic.Pipe child : pipe.getChildren()) {
            PipeRef childRef;
            if (!map.containsKey(child.getUniqueID())) {
                childRef = buildAndMapPipes(child);
            } else {
                childRef = map.get(child.getUniqueID());
            }
            pipeRef.addChild(childRef);
        }
        return pipeRef;
    }
    private PipeRef buildPipe(Schematic.Pipe pipe) {
        ActorRef actorRef;
        if (pipe.hasWrapper()){
            List<Class> classes = pipe.getWrappers();
            Class outermost = classes.get(classes.size() - 1);
            classes.remove(classes.size() - 1);
            classes.add(0, pipe.getClazz());
            actorRef = getContext().actorOf(Props.create(outermost, classes));
        } else {
            actorRef = getContext().actorOf(Props.create(pipe.getClazz()));
        }
        return new PipeRef(pipe.getUniqueID(), actorRef);
    }

    private ActorRef buildErrorHandler(Schematic.ErrorHandler errorHandler) {
        return getContext().actorOf(Props.create(errorHandler.getClazz()));
    }

    private class PipeRef {
        private String id;
        private ActorRef actorRef;
        private ActorRef errorRef = null;
        private List<PipeRef> childrenRefs = new ArrayList<>();
        public PipeRef(String id, ActorRef ref) {
            this.id = id;
            this.actorRef = ref;
        }
        public ActorRef getActorRef(){
            return this.actorRef;
        }
        public void addChild(PipeRef child) {
            this.childrenRefs.add(child);
        }
        public List<PipeRef> getChildren() {
            return this.childrenRefs;
        }
        public Boolean hasChildren() {
            return this.childrenRefs.size() > 0;
        }
        public String getId(){
            return this.id;
        }
        public void setErrorHandler(ActorRef errorRef) {
            this.errorRef = errorRef;
        }
        public ActorRef getErrorHandler() {
            return this.errorRef;
        }
        public Boolean hasErrorHandler() {
            return this.errorRef != null;
        }
    }
}
