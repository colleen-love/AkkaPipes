package com.collin.pipe.concurrency;

import akka.actor.ActorRef;
import akka.routing.*;
import com.collin.pipe.stereotype.WrapperPipe;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * This wrapper acts as a load balancer. It routes incoming messages to a pool of
 * inner pipes of a type decided at construction.
 * @param <I> The type of objects to come in.
 */
public class LoadBalancingPipeWrapper<I> extends WrapperPipe<I> {

    private Class routingLogic = SmallestMailboxRoutingLogic.class;
    private Integer numberOfRoutees = 4;
    private Router router;

    /**
     * Creates a new pipe wrapper.
     *
     * @param innerPipes The type of objects that this pipe will contain.
     */
    public LoadBalancingPipeWrapper(List<Class> innerPipes) {
        super(innerPipes);
        initSystem();
    }
    @SuppressWarnings("unchecked")
    private void initSystem() {
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
     * @param i The incoming message that's being routed.
     */
    @Override
    public void ingest(I i) {
        router.route(i, getSender());
    }
}
