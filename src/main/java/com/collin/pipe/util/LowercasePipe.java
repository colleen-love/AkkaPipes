package com.collin.pipe.util;

import com.collin.pipe.stereotype.Pipe;

public class LowercasePipe extends Pipe<String, String> {
    @Override
    public String ingest(String s) {
        return s.toLowerCase();
    }
}
