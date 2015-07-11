package com.collin.pipe.test;

import com.collin.pipe.concurrency.LoadBalancingPipeWrapper;
import com.collin.pipe.construction.PipeBuilder;
import com.collin.pipe.construction.PipeOpening;
import com.collin.pipe.construction.PipeSystem;
import com.collin.pipe.construction.Schematic;
import com.collin.pipe.util.LogStringPipe;

public class Test {
    public static void main(String[] args) {
        Schematic schematic = new Schematic(LogStringPipe.class);
        Schematic.Pipe logString1 = schematic.getRoot();
        Schematic.Pipe logString2 = logString1.addChild(LogStringPipe.class);
        logString2.wrap(LoadBalancingPipeWrapper.class);
        Schematic.Pipe logString3 = logString1.addChild(LogStringPipe.class);
        Schematic.Pipe logString4 = logString2.addChild(LogStringPipe.class);
        logString4.addParent(logString3);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        opening.put("Hey 0");
    }
}
