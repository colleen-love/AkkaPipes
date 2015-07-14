package com.scangarella.pipe.example.pipe;

import com.scangarella.pipe.stereotype.Pipe;
import com.scangarella.pipe.transmission.ErrorMessage;

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
        if (s.toUpperCase().equals(s)) {
            reportError(new ErrorMessage(Error.class, "You're yelling"));
            return null;
        } else {
            return s;
        }
    }
}
