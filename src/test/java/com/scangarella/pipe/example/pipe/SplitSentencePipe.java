package com.scangarella.pipe.example.pipe;

import com.scangarella.pipe.stereotype.MultiPipe;

import java.util.Arrays;

public class SplitSentencePipe extends MultiPipe<String, String> {
    @Override
    public Iterable<String> ingest(String s) {
        return Arrays.asList(s.split(" "));
    }
}
