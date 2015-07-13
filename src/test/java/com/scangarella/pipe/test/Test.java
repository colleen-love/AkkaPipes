package com.scangarella.pipe.test;

import com.scangarella.pipe.concurrency.LoadBalancingPipeWrapper;
import com.scangarella.pipe.construction.PipeBuilder;
import com.scangarella.pipe.construction.PipeOpening;
import com.scangarella.pipe.construction.PipeSystem;
import com.scangarella.pipe.construction.Schematic;
import com.scangarella.pipe.util.LogStringPipe;
import com.scangarella.pipe.util.LowercasePipe;
import com.scangarella.pipe.util.UppercasePipe;

public class Test {
    public static void main(String[] args) {
        Schematic schematic = new Schematic(LogStringPipe.class);
        Schematic.Pipe logString1 = schematic.getRoot();
        Schematic.Pipe uppercase = logString1.addChild(UppercasePipe.class);
        Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
        Schematic.Pipe lowercase = logString1.addChild(LowercasePipe.class);
        Schematic.Pipe logString2 = uppercase.addChild(LogStringPipe.class);
        lowercase.addChild(logString2);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        opening.put("Hello, world.");
    }
}
