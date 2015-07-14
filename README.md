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

    Schematic schematic = new Schematic(LogIfFrienlyPipe.class);
    Schematic.Pipe logIfFriendly1 = schematic.getRoot();
    Schematic.Pipe uppercase = logIfFriendly1.addChild(UppercasePipe.class);
`

    logIfFriendly1 -> uppercase

Additionally, pipes can be wrapped by other special wrappable pipes.

    Schematic.Wrapper wrapper = uppercase.wrap(LoadBalancingPipeWrapper.class);
`

    logIfFriendly1 -> wrapper[uppercase]
        
Wrappers can also be wrapped. This particular wrapper acts as a load balancer with several uppercase pipes inside.

Pipes can have multiple children:

    Schematic.Pipe lowercase = logIfFriendly1.addChild(LowercasePipe.class);
`

    logIfFriendly1 -> wrapper[uppercase]
                  \-> lowercase 

Don't send mutable data through multiple children, though. This can create race conditions, hard to find bugs, and inconsistent results.

Pipes can also have multiple parents:

    Schematic.Pipe logIfFriendly2 = uppercase.addChild(LogIfFrienlyPipe.class);
    lowercase.addChild(logIfFriendly2);
`

    logIfFriendly1 -> wrapper[uppercase] -> logIfFriendly2
                  \-> ------- lowercase -->/

Infinite loops are supported in order to enable recursion.

Pipes can also have special error handlers that deal with problems. These can also be wrapped.

    Schematic.ErrorHandler errorHandler1 = logIfFriendly1.setErrorHandler(SimpleErrorHandler.class);
    Schematic.ErrorHandler errorHandler2 = logIfFriendly2.setErrorHandler(SimpleErrorHandler.class);
    Schematic.Wrapper errorWrapper = errorHandler2.wrap(SpinUpPipeWrapper.class);
`

     errorHandler1              errorWrapper[errorHandler2]
          ^                                       ^            
    logIfFriendly1 -> wrapper[uppercase] -> logIfFriendly2
                  \-> ------- lowercase -->/

In order to build the pipeline, pass the schematic into a PipeBuilder.
The pipe builder will need an akka actor system in order to be constructed. There's a default one in the PipeSystem class.

    PipeBuilder builder = new PipeBuilder(PipeSystem.GetSystem());
    PipeOpening<String> opening = builder.build(schematic);
        
This gives you a pipe opening into which you can put things. There's no way for the compiler to catch an error if you declare your PipeOpening of the wrong type, make sure it matches your first pipe's input.

In order to use the pipeline, put something into the opening.

    opening.put("Hello, world.");
    
And ater a short wait, close the system:

    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    PipeSystem.CloseSystem();
        
When this is run, it has the following output:

    Hello, world.
    hello, world.
    class java.lang.Error: You're yelling
    
Or sometimes:

    Hello, world.
    class java.lang.Error: You're yelling
    hello, world.
    
Since this is a concurrent system, the order of operations in parallel pipes can happen in any which way.

You can find this example in the test folder. Want to find out more? There's plenty of information in the wiki.
