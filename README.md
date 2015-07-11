# AkkaPipes
AkkaPipes is a framework for creating concurrent pipelines. It allows you to quickly and easily build scalable data processing flows.


##How it works
Pipes are very easy to make. They have two types: I and O. They also have one public method to impliment, 'ingest'. Ingest takes a single argument of type I and returns type O. Here's a simple pipe example.

    public class UppercasePipe extends Pipe<String, String> {
        @Override
        public String ingest(String s) {
            return s.toUpperCase();
        }
    }
    
After creating a series of pipes for data processing, they are configured in a schematic:

    Schematic schematic = new Schematic(LogStringPipe.class);
    Schematic.Pipe logString1 = schematic.getRoot();
    Schematic.Pipe uppercase = logString1.addChild(UppercasePipe.class);

`

    logString1 -> uppercase

Additionally, pipes can be wrapped by other special wrappable pipes.

    Schematic schematic = new Schematic(LogStringPipe.class);
    Schematic.Pipe logString1 = schematic.getRoot();
    Schematic.Pipe uppercase = logString1.addChild(UppercasePipe.class);
    Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);

`

    logString1 -> wrapper[uppercase]
        
Wrappers can also be wrapped. This particular wrapper acts as a load balancer with several uppercase pipes inside.

Pipes can have multiple children:

    Schematic schematic = new Schematic(LogStringPipe.class);
    Schematic.Pipe logString1 = schematic.getRoot();
    Schematic.Pipe uppercase = logString1.addChild(UppercasePipe.class);
    Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
    Schematic.Pipe lowercase = logString1.addChild(LowercasePipe.class);

`

    logString1 -> wrapper[uppercase]
              \-> lowercase 

Don't send mutable data through multiple children, though. This can create race conditions, hard to find bugs, and inconsistent results.

Pipes can also have multiple parents:

    Schematic schematic = new Schematic(LogStringPipe.class);
    Schematic.Pipe logString1 = schematic.getRoot();
    Schematic.Pipe uppercase = logString1.addChild(UppercasePipe.class);
    Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
    Schematic.Pipe lowercase = logString1.addChild(LowercasePipe.class);
    Schematic.Pipe logString2 = uppercase.addChild(LogStringPipe.class);
    lowercase.addChild(logString2);
    
`

    logString1 -> wrapper[uppercase] -> logString2
              \-> ------- lowercase -->/

Infinite loops are supported in order to enable recursion.

In order to build the pipeline, pass the schematic into a PipeBuilder.
The pipe builder will need an akka actor system in order to be constructed. There's a default one in the PipeSystem class.

        PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening<String> opening = builder.build(schematic);
        
This gives you a pipe opening into which you can put things. There's no way for the compiler to catch an error if you declare your PipeOpening of the wrong type, make sure it matches your first pipe's input.

In order to use the pipeline, put something into the opening.

        opening.put("Hello, world.");
        
This has the following output:

    Hello, world.
    HELLO, WORLD.
    hello, world.
    
Or sometimes:

    Hello, world.
    hello, world.
    HELLO, WORLD.
    
It's concurrent, so the order of operations in branched pipe's can happen in any which way.
      
The pipe builder can also build an 'ended' pipe. This way, after your final pipes are finished processing an object, they sends along the output to the akka actor that you've specified. 

        ActorRef myRef = getActorRef();
        PipeBuilder pipeBuilder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening opening = pipeBuilder.build(schematic, myRef);
        
##Pipe Types

There are a few types of pipes.

1. Pipe: this is a simple pipe. It takes in an object of one type and spits out an object of another type.

2. MultiPipe: this pipe spits out several objects for every one put in. 

3. FilterPipe: this pipe decides whether or not an inputted object will pass through onto the next pipe.

4. SideEffectPipe: this pipe passes through the same object, unchanged. The LogStringPipe is one of these.

5. WrapperPipe: this pipe wraps one of the above types of pipes. It changes how the pipes receive messages. 

##A note about WrapperPipes

These pipes need to be transparent in order to function correctly. When passing data to their inner pipes, they should send messages as their sender. Construction of their inner pipes has also been simplified with the protected class, 'buildInnerPipe().


### todo

Add Unit testing
