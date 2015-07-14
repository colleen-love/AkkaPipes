package com.scangarella.pipe.example.pipe;

import com.scangarella.pipe.stereotype.SideEffectPipe;

public class LogStringPipe extends SideEffectPipe<String> {
    @Override
    public String ingest(String s) {
        System.out.println(s);
        return s;
    }
}
