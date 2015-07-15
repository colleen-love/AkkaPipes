package com.scangarella.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.scangarella.pipe.transmission.InitializationMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This pipe builder class takes an Akka ActorSystem on construction.
 * It then creates pipes based on a Schematic and returns the pipe opening.
 */
public final class PipeBuilder {

    private ActorSystem system;
    private Map<String, PipeRef> map = new HashMap<>();

    /**
     * Creates a new PipeBuilder object within this Akka ActorSystem.
     * @param system The akka actorsystem to use.
     */
    public PipeBuilder(ActorSystem system) {
        this.system = system;
    }

    /**
     * Builds a pipeline based on a Schematic within this builder's actorsystem.
     * The resulting pipeline has no 'end'. Objects disappear into the ether after the final pipe.
     * @param schematic The Schematic used to construct the pipeline.
     * @return The pipeopening used to access this pipeline.
     */
    public PipeOpening build(Schematic schematic) {
        return buildEndedPipe(schematic, null);
    }

    /**
     * Builds an ended pipeline based on a Schematic within this builder's actorsystem.
     * The end is an Akka actor which will handle the resulting data with it's onReceive method.
     * @param schematic The Schematic used to construct the pipeline.
     * @param out the actor to be given the final data.
     * @return The pipeopening used to access this pipeline.
     */
    public PipeOpening buildEndedPipe(Schematic schematic, ActorRef out) {
        PipeOpening opening = new PipeOpening(buildAndMapPipes(schematic.getRoot(), out).getActorRef());
        map.clear();
        return opening;
    }
    private PipeRef buildAndMapPipes(Schematic.Pipe pipe, ActorRef out) {
        PipeRef pipeRef = buildPipe(pipe);
        if (pipe.hasErrorHandler()) {
            PipeRef errorHandler = buildPipe(pipe.getErrorHandler());
            if(errorHandler.isWrapper()) {
                errorHandler.getActorRef().tell(new InitializationMessage(errorHandler.getInnerClasses()), null);
            }
            pipeRef.setErrorHandler(errorHandler.getActorRef());
        }
        map.put(pipeRef.getId(), pipeRef);
        if (pipe.hasChildren()) {
            for (Schematic.Pipe child : pipe.getChildren()) {
                PipeRef childRef;
                if (!map.containsKey(child.getUniqueID())) {
                    childRef = buildAndMapPipes(child, out);
                } else {
                    childRef = map.get(child.getUniqueID());
                }
                pipeRef.addChild(childRef);
            }
        } else {
            if (out != null) {
                pipeRef.addChild(out);
            }
        }
        if(pipeRef.isWrapper()) {
            pipeRef.getActorRef().tell(new InitializationMessage(
                    pipeRef.getInnerClasses(), pipeRef.getChildren(), pipeRef.getErrorHandler()), null);
        } else {
            pipeRef.getActorRef().tell(new InitializationMessage(
                    pipeRef.getChildren(), pipeRef.getErrorHandler()), null);
        }
        return pipeRef;
    }
    private PipeRef buildPipe(Schematic.AbstractPipe pipe) {
        PipeRef ref;
        ActorRef actorRef;
        if (pipe.hasWrapper()){
            List<Class> classes = pipe.getWrappers();
            Class outermost = classes.get(classes.size() - 1);
            classes.remove(classes.size() - 1);
            classes.add(0, pipe.getClazz());
            actorRef = this.system.actorOf(Props.create(outermost));
            ref = new PipeRef(pipe.getUniqueID(), actorRef);
            ref.setInnerClasses(classes);
        } else {
            actorRef = this.system.actorOf(Props.create(pipe.getClazz()));
            ref = new PipeRef(pipe.getUniqueID(), actorRef);
        }
        return ref;
    }
    private class PipeRef {
        private ActorRef actorRef;
        private String id;
        private ActorRef errorRef = null;
        private List<ActorRef> childrenRefs = new ArrayList<>();
        private List<Class> innerClasses = null;
        public PipeRef(String id, ActorRef ref) {
            this.actorRef = ref;
            this.id = id;
        }
        public ActorRef getActorRef(){
            return this.actorRef;
        }
        public void addChild(ActorRef child) {
            this.childrenRefs.add(child);
        }
        public void addChild(PipeRef child) {
            this.childrenRefs.add(child.actorRef);
        }
        public List<ActorRef> getChildren() {
            return this.childrenRefs;
        }
        public String getId() {
            return this.id;
        }
        public Boolean hasChildren() {
            return this.childrenRefs.size() > 0;
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
        public Boolean isWrapper() {
            return this.innerClasses != null;
        }
        public void setInnerClasses(List<Class> classes) {
            this.innerClasses = classes;
        }
        public List<Class> getInnerClasses() {
            return this.innerClasses;
        }
    }
}
