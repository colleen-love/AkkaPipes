package com.scangarella.pipe.example;

import com.scangarella.pipe.concurrency.LoadBalancingPipeWrapper;
import com.scangarella.pipe.construction.*;
import com.scangarella.pipe.example.pipe.LogStringPipe;
import com.scangarella.pipe.example.pipe.LowercasePipe;
import com.scangarella.pipe.example.error.SimpleErrorHandler;
import com.scangarella.pipe.example.pipe.UppercasePipe;

public class Example {
    public static void main(String[] args) {
        Schematic schematic = new Schematic(LogStringPipe.class);
        Schematic.Pipe logString1 = schematic.getRoot();
        Schematic.Pipe uppercase = logString1.addChild(UppercasePipe.class);
        Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
        Schematic.Pipe lowercase = logString1.addChild(LowercasePipe.class);
        Schematic.Pipe logString2 = uppercase.addChild(LogStringPipe.class);
        lowercase.addChild(logString2);
    Schematic.ErrorHandler errorHandler = logString2.setErrorHandler(SimpleErrorHandler.class);
    PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
    PipeOpening<String> opening = builder.build(schematic);
    opening.put("Hello, world.");
    }
}
