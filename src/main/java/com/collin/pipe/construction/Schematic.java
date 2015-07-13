package com.collin.pipe.construction;

import akka.actor.ActorRef;
import com.collin.pipe.stereotype.FilterPipe;
import com.collin.pipe.stereotype.SideEffectPipe;
import com.sun.corba.se.impl.io.TypeMismatchException;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Schematic is a simple tree.
 * Each pipe representation represents a pipe and has a class and a parent.
 * Optionally, the pipe representation can have a wrapper.
 * The root does not have a parent.
 */
public final class Schematic {

    private Pipe root;

    /**
     * Creates a new schematic with the first pipe representation.
     * @param clazz The class of the first pipe.
     */
    public Schematic(Class clazz) {
       root = new Pipe(clazz);
    }

    /**
     * Gets the root (the first pipe representation) of this schematic.
     * @return the root of the schematic.
     */
    public Pipe getRoot() {
        return this.root;
    }

    /**
     * Gets all of the pipes in this pipeline.
     * @return all of the pipes in the pipeline.
     */
    public List<Pipe> allPipes() {
        return find(this.root, new ArrayList<>());
    }

    private List<Pipe> find(Pipe pipe, List<Pipe> pipes) {
        pipes.add(pipe);
        for (Pipe child : pipe.getChildren()) {
            find(child, pipes);
        }
        return pipes;
    }

    /**
     * This represents a pipe.
     * It has a class and children associated with it. Optionally, it has a wrapper class and a list of parents.
     * Also, the pipe will have an actor reference to reference the actual pipe after it has been built.
     */
    public class Pipe extends AbstractPipe {

        private List<Pipe> children = new ArrayList<>();
        private String uniqueID = UUID.randomUUID().toString();

        /**
         * Creates a new pipe representation.
         * @param clazz The class of the pipe to represent.
         * @throws TypeMismatchException A class not of type pipe is created or the parent's
         * 'out' type doesn't match this pipe's 'in' type.
         */
        public Pipe(Class clazz) throws TypeMismatchException {
            if (com.collin.pipe.stereotype.Pipe.class.isAssignableFrom(clazz)) {
                this.clazz = clazz;
            } else {
                throw new TypeMismatchException();
            }
        }

        /**
         * Returns this pipe's unique identifier.
         * @return the unique ID.
         */
        public String getUniqueID() {
            return this.uniqueID;
        }

        /**
         * Adds a child to the pipe's children.
         * @param clazz The class of the child to be added.
         * @return The representation of the pipe's child.
         * @throws TypeMismatchException the parent's 'out' type doesn't match this pipe's 'in' type.
         */
        public Pipe addChild(Class clazz) throws TypeMismatchException {
            Pipe child = new Pipe(clazz);
            checkClassCompatibility(this, child);
            this.children.add(child);
            return child;
        }

        /**
         * Adds a child to the pipe's children.
         * @param child the preexisting Pipe to add as a child.
         * @return The representation of the pipe's child.
         * @throws TypeMismatchException the parent's 'out' type doesn't match this pipe's 'in' type.
         */
        public Pipe addChild(Pipe child) throws TypeMismatchException {
            checkClassCompatibility(this, child);
            this.children.add(child);
            return child;
        }

        /**
         * Returns this pipe's children.
         * @return A list of this pipe rep's children.
         */
        public List<Pipe> getChildren() {
            return this.children;
        }

        /**
         * Returns whether or not the pipe representation has children.
         * @return True if the pipe representation has at least on child, false otherwise.
         */
        public Boolean hasChildren() {
            return !this.children.isEmpty();
        }

        @SuppressWarnings("unchecked")
        private void checkClassCompatibility(Pipe parent, Pipe child) throws TypeMismatchException {
            if (parent != null && child != null) {
                if(!child.getInType().isAssignableFrom(parent.getOutType())) {
                    throw new TypeMismatchException();
                }
            }
        }

        private Class getInType() {
            return (Class) ((ParameterizedType) this.clazz.getGenericSuperclass())
                    .getActualTypeArguments()[0];
        }

        private Class getOutType() {
            Class genericOutParameter;
            if (FilterPipe.class.isAssignableFrom(this.clazz) || SideEffectPipe.class.isAssignableFrom(this.clazz)){
                genericOutParameter = (Class) ((ParameterizedType) this.clazz.getGenericSuperclass())
                        .getActualTypeArguments()[0];
            } else {
                genericOutParameter = (Class) ((ParameterizedType) this.clazz.getGenericSuperclass())
                        .getActualTypeArguments()[1];
            }
            return genericOutParameter;
        }
    }

    /**
     * A wrapper representation in the schematic
     */
    public class Wrapper extends AbstractPipe {

        /**
         * Creates a new wrapper.
         * @param clazz the class of the wrapper
         */
        public Wrapper(Class clazz) {
            this.clazz = clazz;
        }
    }
    private abstract class AbstractPipe {

        /**
         * the class of the pipe
         */
        protected Class clazz = null;

        /**
         * the wrapper of the pipe
         */
        protected Wrapper wrapper = null;

        /**
         * Returns this pipe's class.
         * @return This pipe's class.
         */
        public Class getClazz() {
            return this.clazz;
        }

        /**
         * Wraps the pipe with a wrapper
         * @param clazz the class of the wrapper
         * @return The wrapper's object.
         * @throws UnsupportedOperationException If the pipe already has a wrapper.
         */
        public Wrapper wrap(Class clazz) throws UnsupportedOperationException {
            if (this.hasWrapper()) {
                throw new UnsupportedOperationException();
            }
            this.wrapper = new Wrapper(clazz);
            return this.wrapper;
        }

        /**
         * Returns whether or not the pipe has a wrapper.
         * @return True if the pipe has a wrapper, false otherwise.
         */
        public boolean hasWrapper() {
            return this.wrapper != null;
        }

        /**
         * Gets the pipe's wrapper
         * @return The wrapper of the pipe.
         */
        public Wrapper getWrapper() {
            return this.wrapper;
        }

        /**
         * Gets all wrappers, including the wrappers of this pipe's wrapper, etc.
         * @return A list of classes, with element 0 being the class of this pipe,
         * element 1 being the class of this pipe's wrapper, element 2 being the class
         * of this pipe's wrapper's wrapper, etc.
         */
        public List<Class> getWrappers() {
            Wrapper wrapper = this.getWrapper();
            List<Class> wrappers = new ArrayList<>();
            while(wrapper != null) {
                wrappers.add(wrapper.getClazz());
                wrapper = wrapper.getWrapper();
            }
            return wrappers;
        }
    }
}