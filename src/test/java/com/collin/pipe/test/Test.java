package com.collin.pipe.test;

import com.collin.pipe.concurrency.LoadBalancingPipeWrapper;
import com.collin.pipe.construction.PipeBuilder;
import com.collin.pipe.construction.PipeOpening;
import com.collin.pipe.construction.PipeSystem;
import com.collin.pipe.construction.Schematic;
import com.collin.pipe.util.LogStringPipe;
import com.collin.pipe.util.LowercasePipe;
import com.collin.pipe.util.UppercasePipe;

public class Test {
    public static void main(String[] args) {
        Schematic schematic = new Schematic(LogStringPipe.class);
        Schematic.Pipe logString1 = schematic.getRoot();
        Schematic.Pipe uppercase = logString1.addChild(UppercasePipe.class);
        Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
        Schematic.Pipe lowercase = logString1.addChild(LowercasePipe.class);
        Schematic.Pipe logString2 = uppercase.addChild(LogStringPipe.class);
        logString2.addParent(lowercase);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        opening.put("Hello, world.");
    }
}
