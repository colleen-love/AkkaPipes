package com.scangarella.pipe.construction;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * This pipe builder class takes an Akka ActorSystem on construction.
 * It then creates pipes based on a Schematic and returns the pipe opening.
 */
public final class PipeBuilder {

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
        return new PipeOpening(system.actorOf(Props.create(Pipeline.class, schematic)));
    }

    /**
     * Builds an ended pipeline based on a Schematic within this builder's actorsystem.
     * The end is an Akka actor which will handle the resulting data with it's onReceive method.
     * @param schematic The Schematic used to construct the pipeline.
     * @param out the actor to be given the final data.
     * @return The pipeopening used to access this pipeline.
     */
    public PipeOpening buildEndedPipe(Schematic schematic, ActorRef out) {
        return new PipeOpening(system.actorOf(Props.create(Pipeline.class, schematic, out)));
    }
}
