package com.scangarella.pipe.util;

import com.scangarella.pipe.stereotype.Pipe;

/**
 * An example class that makes a string uppercase
 */
public class UppercasePipe extends Pipe<String, String> {
    /**
     * Turns a string into it's uppercase.
     * @param s The initial string.
     * @return It's uppercase brethren.
     */
    @Override
    public String ingest(String s) {
        return s.toUpperCase();
    }
}
