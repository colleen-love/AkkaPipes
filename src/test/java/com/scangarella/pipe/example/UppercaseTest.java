package com.scangarella.pipe.example;

import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.scangarella.pipe.construction.PipeSystem;
import com.scangarella.pipe.example.pipe.UppercasePipe;
import static org.junit.Assert.*;
import org.junit.Test;

public class UppercaseTest {

    private final static Props props = Props.create(UppercasePipe.class);
    private final static TestActorRef<UppercasePipe> ref = TestActorRef.create(PipeSystem.GetSystem(), props, "testA");
    private final static UppercasePipe pipe = ref.underlyingActor();

    @Test
    public void testUppercaseEquals() {
        assertEquals("HELLO", pipe.ingest("Hello"));
    }
}
