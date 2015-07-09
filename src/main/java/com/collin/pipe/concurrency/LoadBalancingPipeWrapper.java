package com.collin.pipe.concurrency;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.*;
import com.collin.pipe.stereotype.WrapperPipe;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This wrapper acts as a load balancer. It routes incoming messages to a pool of
 * inner pipes of a type decided at construction.
 * @param <I> The type of objects to come in.
 * @param <O> The type of objects to go out.
 */
public class LoadBalancingPipeWrapper<I, O> extends WrapperPipe<I, O> {

    private Class routingLogic = SmallestMailboxRoutingLogic.class;
    private Integer numberOfRoutees = 4;
    private Router router;

    /**
     * Creates a new instance of the pipe wrapper.
     * @param pipe The type of pipe that this class will wrap.
     * @param downstreamPipes The pipes to which the resultant object should be routed.
     */
    public LoadBalancingPipeWrapper(Class pipe, List<ActorRef> downstreamPipes){
        super(pipe, downstreamPipes);
        initSystem(this.routingLogic, this.numberOfRoutees);
    }

    /**
     * Initializes the router pool.
     * @param routingLogic The logic to be used for routing.
     * @param numberOfRoutees The number of routees to exist in the pool.
     */
    @SuppressWarnings("unchecked")
    private void initSystem(Class routingLogic, Integer numberOfRoutees) {
        try {
            List<Routee> routees = new ArrayList<>();
            for (int i = 0; i < numberOfRoutees; i++) {
                ActorRef r = getContext().actorOf(Props.create(this.innerPipe, this.downstreamPipes));
                getContext().watch(r);
                routees.add(new ActorRefRoutee(r));
            }
            router = new Router((RoutingLogic) routingLogic.getConstructor().newInstance(), routees);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the wrapper receives an incoming object.
     * It routes the object to the wrapper's routees.
     * @param message The message to be received.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final void onReceive(Object message) {
        I in = (I) message;
        router.route(in, sender());
    }
}
