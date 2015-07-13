package com.scangarella.pipe.util;

import com.scangarella.pipe.stereotype.SideEffectPipe;

/**
 * A utility side-effect-pipe to log a string to the console.
 */
public class LogStringPipe extends SideEffectPipe<String> {

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
