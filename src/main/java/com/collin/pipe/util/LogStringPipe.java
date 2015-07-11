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
        String[] split = s.split(" ");
        Integer i = Integer.parseInt(split[1]);
        s = split[0] + " " + (i + 1);
        System.out.println(s + " : " + getSelf());
        return s;
    }
}
