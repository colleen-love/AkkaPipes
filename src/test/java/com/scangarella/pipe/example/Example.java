package com.scangarella.pipe.example;

import com.scangarella.pipe.concurrency.LoadBalancingPipeWrapper;
import com.scangarella.pipe.concurrency.SpinUpPipeWrapper;
import com.scangarella.pipe.construction.PipeBuilder;
import com.scangarella.pipe.construction.PipeOpening;
import com.scangarella.pipe.construction.PipeSystem;
import com.scangarella.pipe.construction.Schematic;
import com.scangarella.pipe.example.pipe.*;
import com.scangarella.pipe.example.error.SimpleExceptionHandler;

public class Example {
    public static void main(String[] args) {
        ReadMeExample();
        MultiPipeExample();
        ConfigurationExample();
    }
    /**
     * A simple example to show schematic configuration.
     *
     *               errorHandler1              errorWrapper[errorHandler2]
     *                    ^                                       ^
     *   opening -> logIfFriendly1 -> wrapper[uppercase] -> logIfFriendly2
     *                            \-> ------- lowercase -->/
     */
    private static void ReadMeExample() {
        Schematic schematic = new Schematic(LogIfFriendlyPipe.class);
        Schematic.Pipe logIfFriendly1 = schematic.getRoot();
        Schematic.Pipe uppercase = logIfFriendly1.addChild(UppercasePipe.class);
        Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
        Schematic.Pipe lowercase = logIfFriendly1.addChild(LowercasePipe.class);
        Schematic.Pipe logIfFriendly2 = uppercase.addChild(LogIfFriendlyPipe.class);
        lowercase.addChild(logIfFriendly2);
        Schematic.ExceptionHandler errorHandler1 = logIfFriendly1.setExceptionHandler(SimpleExceptionHandler.class);
        Schematic.ExceptionHandler errorHandler2 = logIfFriendly2.setExceptionHandler(SimpleExceptionHandler.class);
        Schematic.Wrapper errorWrapper = errorHandler2.wrap(SpinUpPipeWrapper.class);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        opening.put("Hello, world.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PipeSystem.CloseSystem();
    }

    /**
     *   opening -> LogStringPipe -> SplitSentencePipe -> LogStringPipe
     */
    private static void MultiPipeExample() {
        Schematic schematic = new Schematic(LogStringPipe.class);
        schematic.getRoot()
                .addChild(SplitSentencePipe.class)
                .addChild(LogStringPipe.class);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        opening.put("Hello world");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PipeSystem.CloseSystem();
    }

    /**
     *   opening -> LogStringPipe -> LowercasePipe -> LogStringPipe -> LowercasePipe -> LogIfFriendlyPipe
     *                           \-> UppercasePipe ->/
     */
    private static void ConfigurationExample() {
        Schematic schematic = new Schematic(LogStringPipe.class);
        Schematic.Pipe root = schematic.getRoot();
        Schematic.Pipe lowercase = root.addChild(LowercasePipe.class);
        Schematic.Pipe uppercase = root.addChild(UppercasePipe.class);
        Schematic.Pipe log = lowercase.addChild(LogStringPipe.class);
        uppercase.addChild(log);
        log.addChild(LowercasePipe.class)
                .addChild(LogIfFriendlyPipe.class);
        log.wrap(LoadBalancingPipeWrapper.class);
        log.setExceptionHandler(SimpleExceptionHandler.class);
        log.clearExceptionHandler();
        log.clearWrapper();
        schematic.setGlobalExceptionHandler(SimpleExceptionHandler.class);
        schematic.setGlobalWrapper(LoadBalancingPipeWrapper.class);
        Schematic.Wrapper wrapper = uppercase.wrap(SpinUpPipeWrapper.class);
        wrapper.wrap(LoadBalancingPipeWrapper.class);
        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        opening.put("Hello world");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PipeSystem.CloseSystem();
    }
}
