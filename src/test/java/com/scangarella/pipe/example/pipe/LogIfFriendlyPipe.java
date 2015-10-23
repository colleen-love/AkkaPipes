package com.scangarella.pipe.example.pipe;

import com.scangarella.pipe.stereotype.SideEffectPipe;
import com.scangarella.pipe.transmission.ExceptionMessage;

/**
 * A utility side-effect-pipe to log a string to the console.
 */
public class LogIfFriendlyPipe extends SideEffectPipe<String> {

    /**
     * Logs the parameter to the console.
     * @param s The string to be logged.
     * @return The same string.
     */
    @Override
    public String ingest(String s) {
        if (s.toUpperCase().equals(s)) {
            reportError(new ExceptionMessage(new Exception("You're yelling!")));
            return null;
        } else {
            System.out.println(s);
            return s;
        }
    }
}
