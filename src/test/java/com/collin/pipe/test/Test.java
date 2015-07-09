package com.collin.pipe.test;

import com.collin.pipe.concurrency.LoadBalancingPipeWrapper;
import com.collin.pipe.construction.Schematic;
import com.collin.pipe.util.ByteArrayToStringPipe;
import com.collin.pipe.util.LogStringPipe;

public class Test {
    public static void main(String[] args) {
        Schematic ps = new Schematic(LogStringPipe.class);
        ps.getRoot().addChild(LogStringPipe.class, LoadBalancingPipeWrapper.class)
                .addChild(ByteArrayToStringPipe.class);

    }
}
