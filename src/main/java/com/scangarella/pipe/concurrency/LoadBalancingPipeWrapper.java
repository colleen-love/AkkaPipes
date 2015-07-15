package com.scangarella.pipe.concurrency;

import akka.actor.ActorRef;
import akka.routing.*;
import com.scangarella.pipe.stereotype.WrapperPipe;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This wrapper acts as a load balancer. It routes incoming messages to a pool of
 * inner pipes of a type decided at construction.
 */
public class LoadBalancingPipeWrapper extends WrapperPipe {

    private Class routingLogic = SmallestMailboxRoutingLogic.class;
    private Integer numberOfRoutees = 4;
    private Router router;

    /**
     * Creates the router and it's routees.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initSystem() {
        List<Routee> routees = new ArrayList<>();
        for (int i = 0; i < this.numberOfRoutees; i++) {
            ActorRef r = buildInnerPipe();
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }
        try {
            this.router = new Router((RoutingLogic) this.routingLogic.getConstructor().newInstance(), routees);
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
     * Routes the incoming message to it's routees.
     * @param message The incoming message that's being routed.
     */
    @Override
    public void ingest(Object message) {
        router.route(message, getSender());
    }
}
