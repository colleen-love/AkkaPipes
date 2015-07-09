package com.collin.pipe.construction;

import akka.actor.ActorSystem;

/**
 * A pipe system. This holds a reference to a default actor system called 'pipeline'.
 */
public class PipeSystem {
    /**
     * The default actor system.
     */
    private static ActorSystem system = ActorSystem.create("pipeline");

    /**
     * Gets the default actor system.
     * @return the default actor system.
     */
    public static ActorSystem GetSystem() {
        return system;
    }
}
