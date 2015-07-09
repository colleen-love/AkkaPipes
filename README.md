# AkkaPipes
A simple concurrency pipeline framework using akka actors.

##How it works
Welcome to akka pipes! This framework uses akka actors to create a concurrent pipeline. Each pipe can operate on an object independently of the others.

Pipes are configured in a pipeline schematic:

        Schematic schematic = new Schematic(LogStringPipe.class);
        Schematic.PipeRep logString1 = schematic.getRoot();
        Schematic.PipeRep logString2 = logString1.addChild(LogStringPipe.class);

This looks like this:

    logString1 -> logString2

Additionally, pipes can be wrapped by other special wrappable pipes.

        Schematic schematic = new Schematic(LogStringPipe.class, LoadBalancingPipeWrapper.class);
        Schematic.PipeRep logString1 = schematic.getRoot();
        Schematic.PipeRep logString2 = logString1.addChild(LogStringPipe.class);

This looks like this:

    loadBalancingPipeWrapper[logString1] -> logString2
        
This particular wrapper acts as a load balancer with several logStringPipes inside.

Pipes can have multiple children:

        Schematic schematic = new Schematic(LogStringPipe.class, LoadBalancingPipeWrapper.class);
        Schematic.PipeRep logString1 = schematic.getRoot();
        Schematic.PipeRep logString2 = logString1.addChild(LogStringPipe.class);
        Schematic.PipeRep logString3 = logString1.addChild(LogStringPipe.class);

This looks like this:

    loadBalancingPipeWrapper[logString1] -> logString2
                                        \-> logString3

Pipes can also have multiple parents:

        Schematic schematic = new Schematic(LogStringPipe.class, LoadBalancingPipeWrapper.class);
        Schematic.PipeRep logString1 = schematic.getRoot();
        Schematic.PipeRep logString2 = logString1.addChild(LogStringPipe.class);
        Schematic.PipeRep logString3 = logString1.addChild(LogStringPipe.class);
        Schematic.PipeRep logString4 = logString2.addChild(LogStringPipe.class);
        logString4.addParent(logString3).
  
This looks like this:

    loadBalancingPipeWrapper[logString1] -> logString2 -> logString4
                                        \-> logString3 ->/
          
Infinite loops are not supported, though. They don't build properly.

Speaking of building; in order to construct a pipeline, pass the schematic into a PipeBuilder.
The pipe builder will need an akka actor system in order to be constructed.

        PipeBuilder pipeBuilder = new PipeBuilder(PipeSystem.GetSystem());
        PipeOpening opening = pipeBuilder.build(schematic);
        
This gives you a pipe opening into which you can put things.

        opening.put("Hello, world.");
      
The pipe builder can also build an 'ended' pipe. This way, after your pipe is finished processing an object, it sends along the final pipe's output to the akka actor that you've specified. 

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

##Subclassing Pipes

Creating pipes is easy. Here's the source for that infamous LogStringPipe:

        public class LogStringPipe extends SideEffectPipe<String> {
            public LogStringPipe(List<ActorRef> downstreamPipes) {
                super(downstreamPipes);
            }
            @Override
            public String ingest(String s) {
                System.out.println(s);
                return s;
            }
        }

That constructor needs to be there to set the downstream pipes.
The only other thing to do is to override the ingest method. 

### todo

Add Unit testing

Do it with scala conventions

Configure for infinitely wrappable wrappables.
