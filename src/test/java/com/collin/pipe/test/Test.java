package com.collin.pipe.test;

import com.collin.pipe.concurrency.LoadBalancingPipeWrapper;
import com.collin.pipe.construction.PipeBuilder;
import com.collin.pipe.construction.PipeOpening;
import com.collin.pipe.construction.PipeSystem;
import com.collin.pipe.construction.Schematic;
import com.collin.pipe.construction.Schematic.Pipe;
import com.collin.pipe.construction.Schematic.Wrapper;
import com.collin.pipe.util.LogStringPipe;
import com.collin.pipe.util.LowercasePipe;
import com.collin.pipe.util.UppercasePipe;

public class Test {
    public static void main(String[] args) {
        Schematic schematic = new Schematic(LogStringPipe.class);
        Pipe logString1 = schematic.getRoot();
        Pipe uppercase = logString1.addChild(UppercasePipe.class);
        Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
        Pipe lowercase = logString1.addChild(LowercasePipe.class);
        Pipe logString2 = uppercase.addChild(LogStringPipe.class);
        lowercase.addChild(logString2);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        opening.put("Hello, world.");
    }
}
