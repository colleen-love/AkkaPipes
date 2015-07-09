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
    private PipeRep root;

    /**
     * Creates a new schematic with the first pipe representation.
     * @param clazz The class of the first pipe.
     */
    public Schematic(Class clazz) {
       root = new PipeRep(clazz);
    }

    /**
     * Creates a new schematic with a first pipe and a pipe wrapper.
     * @param clazz The class of the first pipe.
     * @param wrapperClass The type of wrapper for the first pipe.
     */
    public Schematic(Class clazz, Class wrapperClass) {
        root = new PipeRep(clazz, wrapperClass);
    }

    /**
     * Gets the root (the first pipe representation) of this schematic.
     * @return the root of the schematic.
     */
    public PipeRep getRoot() {
        return this.root;
    }

    /**
     * This represents a pipe.
     * It has a class and children associated with it. Optionally, it has a wrapper class and a list of parents.
     * Also, the pipe will have an actor reference to reference the actual pipe after it has been built.
     */
    public class PipeRep {
        /**
         * The type of class to wrap the pipe. Optional.
         */
        private Class wrapperClazz = null;
        /**
         * The type of class that this pipe is.
         */
        private Class clazz;
        /**
         * The pipe's parents. It may have more than one.
         * Each parent's output type must equal this pipe's input type.
         */
        private List<PipeRep> parents = new ArrayList<>();
        /**
         * This pipe's children. It may have more than one.
         * Each child's input type must equal this pipe's output type.
         */
        private List<PipeRep> children = new ArrayList<>();
        /**
         * An actor reference for the pipe, once it has been built.
         */
        private ActorRef self = null;

        /**
         * If the class is not of type 'PipeRep' an error will be thrown.
         * Creates a new pipe representation.
         * @param clazz The class of pipe to represent.
         */
        public PipeRep(Class clazz){
            new PipeRep(clazz, null, null);
        }

        /**
         * Creates a new pipe representation.
         * If the class is not of type 'PipeRep' an error will be thrown.
         * If the parent's out type doesn't match the pipe's in type, an error will be thrown.
         * @param clazz The class of pipe to represent.
         * @param parent the parent of the pipe.
         */
        public PipeRep(Class clazz, PipeRep parent) throws TypeMismatchException {
            new PipeRep(clazz, null, parent);
        }

        /**
         * Creates a new pipe representation.
         * If the class is not of type 'PipeRep' an error will be thrown.
         * @param clazz The class of the pipe to represent.
         * @param wrapperClazz The wrapper of the pipe.
         */
        public PipeRep(Class clazz, Class wrapperClazz) {
            new PipeRep(clazz, wrapperClazz, null);
        }

        /**
         * Creates a new pipe representation.
         * If the class is not of type 'PipeRep' an error will be thrown.
         * If the parent's out type doesn't match the pipe's or it's wrapper's in type, an error will be thrown.
         * @param clazz The class of the pipe to represent.
         * @param wrapperClazz The wrapper of the pipe.
         * @param parent The parent of the pipe.
         */
        public PipeRep(Class clazz, Class wrapperClazz, PipeRep parent) throws TypeMismatchException {
            if (com.collin.pipe.stereotype.Pipe.class.isAssignableFrom(clazz)) {
                this.clazz = clazz;
            } else {
                throw new TypeMismatchException();
            }
            if (wrapperClazz != null) {
                if (com.collin.pipe.stereotype.WrapperPipe.class.isAssignableFrom(wrapperClazz)) {
                    this.wrapperClazz = wrapperClazz;
                } else {
                    throw new TypeMismatchException();
                }
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
        public PipeRep addChild(Class clazz) throws TypeMismatchException {
            return addChild(clazz, null);
        }

        /**
         * Adds a child to the pipe's children.
         * If the parent's 'out' type doesn't match the pipe's or it's wrapper's 'in' type, an error will be thrown.
         * @param clazz The class of the child to be added.
         * @param wrapperClazz The wrapper of the new child.
         * @return The representation of the pipe's child.
         */
        public PipeRep addChild(Class clazz, Class wrapperClazz) throws TypeMismatchException {
            checkClassCompatibility(getClazz(), clazz, wrapperClazz);
            PipeRep n = new PipeRep(clazz, wrapperClazz, this);
            this.children.add(n);
            return n;
        }

        /**
         * Checks to see if all of this pipe representation's children have an actor reference.
         * @return True of all children have an actor reference. False otherwise.
         */
        public Boolean childrenPopulated() {
            for (PipeRep child : getChildren()) {
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
         * Returns this pipe's class.
         * @return This pipe's class.
         */
        public Class getClazz() {
            return this.clazz;
        }

        /**
         * Returns this pipe's children.
         * @return A list of this pipe rep's children.
         */
        public List<PipeRep> getChildren() {
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
        public List<PipeRep> getParents() {
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
        public void addParent(PipeRep parent) {
            checkClassCompatibility(parent.getClazz(), this.clazz, this.wrapperClazz);
            this.parents.add(parent);
        }

        /**
         * Get's this pipe representation's wrapper class.
         * @return The pipe's wrapper class.
         */
        public Class getWrapperClazz() { return this.wrapperClazz; }

        /**
         * Returns whether or not the pipe has a wrapper.
         * @return True if the pipe has a wrapper, false otherwise.
         */
        public Boolean hasWrapper() { return this.wrapperClazz != null; }

        /**
         * Sets the pipe representation's ActorRef.
         * @param actorRef The actor ref to set.
         */
        public void setActorRef(ActorRef actorRef) {
            this.self = actorRef;
        }

        /**
         * Get's this pipe representation's actor ref.
         * @return The pipe's actor ref.
         */
        public ActorRef getActorRef() {
            return this.self;
        }

        /**
         * Checks to see if the pipe representation has an actor ref.
         * @return True if this pipe representation has an actor ref, false otherwise.
         */
        public Boolean hasActorRef() {
            return this.self != null;
        }

        /**
         * Checks to see if a parent is compatible with a child.
         * Compatibility is defined if a parent's 'out' type equals a child's 'in' type.
         * @param parent The parent to be checked
         * @param child The child to be checked.
         * @param wrapper The wrapper of the child to be checked.
         */
        private void checkClassCompatibility(Class parent, Class child, Class wrapper) throws TypeMismatchException {
            if (parent != null) {
                Class genericOutParameter;
                if (FilterPipe.class.isAssignableFrom(parent) || SideEffectPipe.class.isAssignableFrom(parent)){
                    genericOutParameter = (Class) ((ParameterizedType) parent.getGenericSuperclass())
                            .getActualTypeArguments()[0];
                } else {
                    genericOutParameter = (Class) ((ParameterizedType) parent.getGenericSuperclass())
                            .getActualTypeArguments()[1];
                }
                if (child != null) {
                    Class genericClassInParameter = (Class) ((ParameterizedType) child.getGenericSuperclass())
                            .getActualTypeArguments()[0];
                    if (genericClassInParameter != genericOutParameter) {
                        throw new TypeMismatchException();
                    }
                }
                if (wrapper != null) {
                    Class genericWrapperInParameter = (Class) ((ParameterizedType) wrapper.getGenericSuperclass())
                            .getActualTypeArguments()[0];
                    if (genericWrapperInParameter != genericOutParameter) {
                        throw new TypeMismatchException();
                    }
                }
            }
        }
    }
}