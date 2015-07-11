package com.collin.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

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
        buildRootFirst(schematic.getRoot());
        PipeOpening opening = new PipeOpening(system.actorOf(Props.create(Pipeline.class, schematic)));
        return opening;
    }

    /**
     * Builds an ended pipeline based on a Schematic within this builder's actorsystem.
     * The end is an Akka actor which will handle the resulting data with it's onReceive method.
     * @param schematic The Schematic used to construct the pipeline.
     * @param out the actor to be given the final data.
     * @return The pipeopening used to access this pipeline.
     */
    public PipeOpening buildEndedPipe(Schematic schematic, ActorRef out) {
        buildRootFirst(schematic.getRoot());
        PipeOpening opening =  new PipeOpening(system.actorOf(Props.create(Pipeline.class, schematic, out)));
        return opening;
    }

    private void buildRootFirst(Schematic.Pipe pipe) {
        buildPipe(pipe);
        pipe.getChildren().forEach(child -> {
            if (!child.hasActorRef()) {
                buildRootFirst(child);
            }
        });
    }

    /**
     * Builds a pipe with a specific end.
     * @param pipe The pipe to be built.
     * @return An actorref of the built pipe.
     */
    private ActorRef buildPipe(Schematic.Pipe pipe) {
        ActorRef child;
        if (pipe.hasWrapper()){
            List<Class> classes = pipe.getWrappers();
            Class outermost = classes.get(classes.size() - 1);
            classes.remove(classes.size() - 1);
            classes.add(0, pipe.getClazz());
            child = system.actorOf(Props.create(outermost, classes));
        } else {
            child = system.actorOf(Props.create(pipe.getClazz()));
        }
        pipe.setActorRef(child);
        return child;
    }
}
