package com.scangarella.pipe.construction;

import akka.actor.ActorSystem;

/**
 * A pipe system. This holds a reference to a default actor system called 'pipeline'.
 */
public final class PipeSystem {

    private static ActorSystem system;

    /**
     * Gets the default actor system.
     * @return the default actor system.
     */
    public static ActorSystem GetSystem() {
        if (system == null) {
            system = ActorSystem.create("pipeline");
        }
        return system;
    }

    /**
     * Closes the default actor system.
     */
    public static void CloseSystem() {
        system.shutdown();
        system = null;
    }
}
