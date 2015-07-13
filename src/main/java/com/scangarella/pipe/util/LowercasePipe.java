package com.scangarella.pipe.util;

import com.scangarella.pipe.stereotype.Pipe;

/**
 * An example pipe to make a string lowercase
 */
public class LowercasePipe extends Pipe<String, String> {

    /**
     * Makes the string lowercase.
     * @param s The initial string
     * @return The string's lowercase brethren.
     */
    @Override
    public String ingest(String s) {
        return s.toLowerCase();
    }
}
