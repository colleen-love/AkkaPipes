package com.collin.pipe.test;

import com.collin.pipe.concurrency.LoadBalancingPipeWrapper;
import com.collin.pipe.construction.PipeBuilder;
import com.collin.pipe.construction.PipeOpening;
import com.collin.pipe.construction.PipeSystem;
import com.collin.pipe.construction.Schematic;
import com.collin.pipe.util.ByteArrayToStringPipe;
import com.collin.pipe.util.LogStringPipe;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, World".getBytes());
        Schematic schematic = new Schematic(ByteArrayToStringPipe.class);
        Schematic.PipeRep logString1 = schematic.getRoot();
        Schematic.PipeRep logString2 = logString1.addChild(LogStringPipe.class);
        logString2.wrap(LoadBalancingPipeWrapper.class);
        Schematic.PipeRep logString3 = logString1.addChild(LogStringPipe.class);
        Schematic.PipeRep logString4 = logString2.addChild(LogStringPipe.class);
        logString4.addParent(logString3);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<Byte[]> opening = builder.build(schematic);
        //opening.put("Hey");
    }
}
