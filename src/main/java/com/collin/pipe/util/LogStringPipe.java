package com.collin.pipe.util;

import akka.actor.ActorRef;
import com.collin.pipe.stereotype.Pipe;
import com.collin.pipe.stereotype.SideEffectPipe;

import java.util.List;

/**
 * A utility side-effect-pipe to log a string to the console.
 */
public class LogStringPipe extends Pipe<String, String> {

    /**
     * Logs the parameter to the console.
     * @param s The string to be logged.
     * @return The same string.
     */
    @Override
    public String ingest(String s) {
        System.out.println(s);
        return s;
    }
}
