package com.collin.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * This pipe builder class takes an Akka ActorSystem on construction.
 * It then creates pipes based on a Schematic and returns the pipe opening.
 */
public final class PipeBuilder {

    /**
     * The Akka ActorSystem into which the pipes are to be created.
     */
    private ActorSystem system;

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
        buildLeavesFirst(schematic.getRoot(), null);
        return new PipeOpening(schematic.getRoot().getActorRef());
    }

    /**
     * Builds an ended pipeline based on a Schematic within this builder's actorsystem.
     * The end is an Akka actor which will handle the resulting data with it's onReceive method.
     * @param schematic The Schematic used to construct the pipeline.
     * @param out the actor to be given the final data.
     * @return The pipeopening used to access this pipeline.
     */
    public PipeOpening buildEndedPipe(Schematic schematic, ActorRef out) {
        buildLeavesFirst(schematic.getRoot(), out);
        return new PipeOpening(schematic.getRoot().getActorRef());
    }

    /**
     * Construction method. Uses recursion to build a pipeline, starting with the
     * leaves and moving back up to the root.
     * @param pipe The pipe to be built.
     * @param out The actorref to receive notification if this in an ended pipe.
     */
    private void buildLeavesFirst(Schematic.PipeRep pipe, ActorRef out){
        if (pipe.hasChildren()) {
            pipe.getChildren().forEach(child -> buildLeavesFirst(child, out));
        } else {
            buildPipe(pipe, out);
            if (pipe.hasParents()) {
                pipe.getParents().forEach(parent -> tryToBuildParent(parent));
            }
        }
    }

    /**
     * Builds a pipe with a specific end.
     * @param pipe The pipe to be built.
     * @param downstream the downstream pipes of the pipe to be built.
     * @return An actorref of the built pipe.
     */
    private ActorRef buildPipe(Schematic.PipeRep pipe, ActorRef downstream) {
        List<ActorRef> downstreamList = new ArrayList<>();
        if(downstream != null) {
            downstreamList.add(downstream);
        }
        return buildPipe(pipe, downstreamList);
    }

    /**
     * Builds a pipe with a specific end.
     * @param pipe The pipe to be built.
     * @param downstream the downstream pipes of the pipe to be built.
     * @return An actorref of the built pipe.
     */
    private ActorRef buildPipe(Schematic.PipeRep pipe, List<ActorRef> downstream) {
        ActorRef child;
        if (pipe.hasWrapper()){
            List<Class> wrappers = pipe.getWrappers();
            Class outermost = wrappers.get(wrappers.size() - 1);
            wrappers.remove(wrappers.size() - 1);
            wrappers.add(0, pipe.getClazz());
            child = system.actorOf(Props.create(outermost, wrappers, downstream ));
        } else {
            child = system.actorOf(Props.create(pipe.getClazz(), downstream));
        }
        pipe.setActorRef(child);
        return child;
    }

    /**
     * Tries to build a pipe's parent. If all of the parents children are built, it is built.
     * @param parent The parent pipe to be built.
     */
    private void tryToBuildParent(Schematic.PipeRep parent) {
        if (parent.childrenPopulated()) {
            List<ActorRef> children = parent.getChildrenRefs();
            buildPipe(parent, children);
            if (parent.hasParents()) {
                parent.getParents().forEach(grandparent -> tryToBuildParent(grandparent));
            }
        }
    }
}
