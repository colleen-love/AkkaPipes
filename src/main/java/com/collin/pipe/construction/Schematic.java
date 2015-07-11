package com.collin.pipe.construction;

import akka.actor.ActorRef;
import com.collin.pipe.stereotype.FilterPipe;
import com.collin.pipe.stereotype.SideEffectPipe;
import com.sun.corba.se.impl.io.TypeMismatchException;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * This Schematic is a simple tree.
 * Each pipe representation represents a pipe and has a class and a parent.
 * Optionally, the pipe representation can have a wrapper.
 * The root does not have a parent.
 */
public final class Schematic {
    /**
     * The root of the schematic, the first representation.
     */
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
    @Override
    public String toString() {
        return this.root.toString();
    }

    /**
     * This represents a pipe.
     * It has a class and children associated with it. Optionally, it has a wrapper class and a list of parents.
     * Also, the pipe will have an actor reference to reference the actual pipe after it has been built.
     */
    public class Pipe extends AbstractPipe {
        /**
         * The pipe's parents. It may have more than one.
         * Each parent's output type must equal this pipe's input type.
         */
        private List<Pipe> parents = new ArrayList<>();
        /**
         * This pipe's children. It may have more than one.
         * Each child's input type must equal this pipe's output type.
         */
        private List<Pipe> children = new ArrayList<>();
        /**
         * An actor reference for the pipe, once it has been built.
         */
        private ActorRef actorRef = null;
        /**
         * If the class is not of type 'Pipe' an error will be thrown.
         * Creates a new pipe representation.
         * @param clazz The class of pipe to represent.
         */
        public Pipe(Class clazz){
            this(clazz, null);
        }

        /**
         * Creates a new pipe representation.
         * If the class is not of type 'Pipe' an error will be thrown.
         * If the parent's out type doesn't match the pipe's or it's wrapper's in type, an error will be thrown.
         * @param clazz The class of the pipe to represent.
         * @param parent The parent of the pipe.
         */
        public Pipe(Class clazz, Pipe parent) throws TypeMismatchException {
            if (com.collin.pipe.stereotype.Pipe.class.isAssignableFrom(clazz)) {
                this.clazz = clazz;
            } else {
                throw new TypeMismatchException();
            }
            if (parent != null) {
                this.parents.add(parent);
            }
        }

        /**
         * Adds a child to the pipe's children.
         * If the parent's 'out' type doesn't match the pipe's or it's wrapper's 'in' type, an error will be thrown.
         * @param clazz The class of the child to be added.
         * @return The representation of the pipe's child.
         */
        public Pipe addChild(Class clazz) throws TypeMismatchException {
            Pipe child = new Pipe(clazz, this);
            checkClassCompatibility(this, child);
            this.children.add(child);
            return child;
        }

        public Pipe addChild(Pipe child) throws TypeMismatchException {
            checkClassCompatibility(this, child);
            this.children.add(child);
            return child;
        }

        /**
         * Checks to see if all of this pipe representation's children have an actor reference.
         * @return True of all children have an actor reference. False otherwise.
         */
        public Boolean childrenPopulated() {
            for (Pipe child : getChildren()) {
                if (!child.hasActorRef()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Gets all of the pipe representation's children's actor refs.
         * @return A list of all of the pipe representation's children's actor refs.
         */
        public List<ActorRef> getChildrenRefs() {
            List<ActorRef> refs = new ArrayList<>();
            getChildren().forEach(child -> refs.add(child.getActorRef()));
            return refs;
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

        /**
         * Returns this pipe's parents.
         * @return A list of this pipe representation's parents.
         */
        public List<Pipe> getParents() {
            return this.parents;
        }

        /**
         * Returns whether or not the pipe has parents.
         * @return True if the pipe representation has at least one parent, false otherwise.
         */
        public Boolean hasParents() {
            return !this.parents.isEmpty();
        }

        /**
         * Adds a parent to this to the pipe's parents.
         * If the parent's 'out' type doesn't match this pipe's or it's wrapper's 'in' type, an error will be thrown.
         * @param parent The parent to be added.
         */
        public void addParent(Pipe parent) {
            this.parents.add(parent);
            parent.addChild(this);
        }

        /**
         * Sets the pipe representation's ActorRef.
         * @param actorRef The actor ref to set.
         */
        public void setActorRef(ActorRef actorRef) {
            this.actorRef = actorRef;
        }

        /**
         * Get's this pipe representation's actor ref.
         * @return The pipe's actor ref.
         */
        public ActorRef getActorRef() {
            return this.actorRef;
        }

        /**
         * Checks to see if the pipe representation has an actor ref.
         * @return True if this pipe representation has an actor ref, false otherwise.
         */
        public Boolean hasActorRef() {
            return this.actorRef != null;
        }
        public String getId() {
            return this.actorRef.toString();
        }
        /**
         * Checks to see if a parent is compatible with a child.
         * Compatibility is defined if a parent's 'out' type equals a child's 'in' type.
         * @param parent The parent to be checked
         * @param child The child to be checked.
         */
        private void checkClassCompatibility(Pipe parent, Pipe child) throws TypeMismatchException {
            if (parent != null && child != null) {
                if(child.getInType() != parent.getOutType()) {
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
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.clazz + ": " + this.actorRef);
            for(Pipe child : this.children) {
                sb.append("\n" + child.toString());
            }
            return sb.toString();
        }

    }
    public class Wrapper extends AbstractPipe {
        private AbstractPipe innerPipe;
        public Wrapper(AbstractPipe inner, Class clazz) {
            this.innerPipe = inner;
            this.clazz = clazz;
        }
    }
    private abstract class AbstractPipe {
        /**
         * The type of class that this pipe is.
         */
        protected Class clazz = null;

        protected Wrapper wrapper = null;

        /**
         * Returns this pipe's class.
         * @return This pipe's class.
         */
        public Class getClazz() {
            return this.clazz;
        }

        public Wrapper wrap(Class clazz) {
            this.wrapper = new Wrapper(this, clazz);
            return this.wrapper;
        }
        public boolean hasWrapper() {
            return this.wrapper != null;
        }
        public Wrapper getWrapper() {
            return this.wrapper;
        }
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