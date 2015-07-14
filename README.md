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

    Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);

`

    logString1 -> wrapper[uppercase]
        
Wrappers can also be wrapped. This particular wrapper acts as a load balancer with several uppercase pipes inside.

Pipes can have multiple children:

    Schematic.Pipe lowercase = logString1.addChild(LowercasePipe.class);

`

    logString1 -> wrapper[uppercase]
              \-> lowercase 

Don't send mutable data through multiple children, though. This can create race conditions, hard to find bugs, and inconsistent results.

Pipes can also have multiple parents:

    Schematic.Pipe logString2 = uppercase.addChild(LogStringPipe.class);
    lowercase.addChild(logString2);
    
`

    logString1 -> wrapper[uppercase] -> logString2
              \-> ------- lowercase -->/

Infinite loops are supported in order to enable recursion.

Pipes can also have special error handlers that deal with problems.

    Schematic.ErrorHandler errorHandler = logString2.setErrorHandler(SimpleErrorHandler.class);
    
`

                                        errorHandler
                                             ^            
    logString1 -> wrapper[uppercase] -> logString2
              \-> ------- lowercase -->/

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

###Want to find out more? There's plenty of information in the wiki.
