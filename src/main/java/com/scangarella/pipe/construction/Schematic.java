package com.scangarella.pipe.construction;

import com.scangarella.pipe.error.IncompatibleTypeException;
import com.scangarella.pipe.stereotype.FilterPipe;
import com.scangarella.pipe.stereotype.SideEffectPipe;
import com.scangarella.pipe.stereotype.WrapperPipe;

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
    private ErrorHandler globalErrorHandler;
    private Wrapper globalWrapper;

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

    public ErrorHandler setGlobalErrorHandler(Class clazz) {
        this.globalErrorHandler = new ErrorHandler(clazz);
        allPipes().forEach(pipe -> pipe.setErrorHandler(this.globalErrorHandler));
        return this.globalErrorHandler;
    }

    public Wrapper setGlobalWrapper(Class clazz) {
        this.globalWrapper = new Wrapper(clazz);
        allPipes().forEach(pipe -> pipe.wrap(this.globalWrapper));
        return this.globalWrapper;
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
        private ErrorHandler errorHandler;

        /**
         * Creates a new pipe representation.
         * @param clazz The class of the pipe to represent.
         * @throws IncompatibleTypeException A class not of type pipe is created or the parent's
         * 'out' type doesn't match this pipe's 'in' type.
         */
        public Pipe(Class clazz) throws IncompatibleTypeException {
            if (com.scangarella.pipe.stereotype.AbstractPipe.class.isAssignableFrom(clazz)) {
                this.clazz = clazz;
                if(globalErrorHandler != null) {
                    this.setErrorHandler(globalErrorHandler);
                }
                if(globalWrapper != null) {
                    this.wrap(globalWrapper);
                }
            } else {
                throw new IncompatibleTypeException();
            }
        }

        /**
         * Adds a child to the pipe's children.
         * @param clazz The class of the child to be added.
         * @return The representation of the pipe's child.
         * @throws IncompatibleTypeException the parent's 'out' type doesn't match this pipe's 'in' type.
         */
        public Pipe addChild(Class clazz) throws IncompatibleTypeException {
            return addChild(new Pipe(clazz));
        }

        /**
         * Adds a child to the pipe's children.
         * @param child the preexisting Pipe to add as a child.
         * @return The representation of the pipe's child.
         * @throws IncompatibleTypeException the parent's 'out' type doesn't match this pipe's 'in' type.
         */
        public Pipe addChild(Pipe child) throws IncompatibleTypeException {
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

        public ErrorHandler setErrorHandler(Class clazz) {
            return setErrorHandler(new ErrorHandler(clazz));
        }

        public ErrorHandler setErrorHandler(ErrorHandler errorHandler) {
            if (!com.scangarella.pipe.stereotype.ErrorHandler.class.isAssignableFrom(errorHandler.getClazz())) {
                throw new UnsupportedOperationException();
            }
            this.errorHandler = errorHandler;
            return this.errorHandler;
        }

        public ErrorHandler getErrorHandler() {
            return this.errorHandler;
        }

        public void clearErrorHandler() { this.errorHandler = null; }

        public Boolean hasErrorHandler() {
            return this.errorHandler != null;
        }

        @SuppressWarnings("unchecked")
        private void checkClassCompatibility(Pipe parent, Pipe child) throws IncompatibleTypeException {
            if (parent != null && child != null) {
                if(!child.getInType().isAssignableFrom(parent.getOutType())) {
                    throw new IncompatibleTypeException();
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

    public class ErrorHandler extends AbstractPipe {
        public ErrorHandler(Class clazz) {
            this.clazz = clazz;
        }
    }
    public abstract class AbstractPipe {

        /**
         * the wrapper of the pipe
         */
        protected Wrapper wrapper = null;

        /**
         * the class of the pipe
         */
        protected Class clazz = null;

        protected String uniqueID = UUID.randomUUID().toString();

        /**
         * Returns this pipe's class.
         * @return This pipe's class.
         */
        public Class getClazz() {
            return this.clazz;
        }

        /**
         * Returns this pipe's unique identifier.
         * @return the unique ID.
         */
        public String getUniqueID() {
            return this.uniqueID;
        }

        /**
         * Wraps the pipe with a wrapper
         * @param clazz the class of the wrapper
         * @return The wrapper's object.
         * @throws UnsupportedOperationException If the pipe already has a wrapper.
         */
        public Wrapper wrap(Class clazz) throws IncompatibleTypeException {
            if (!WrapperPipe.class.isAssignableFrom(clazz)) {
                throw new IncompatibleTypeException();
            }
            this.wrapper = new Wrapper(clazz);
            return this.wrapper;
        }

        public Wrapper wrap(Wrapper wrapper) {
            this.wrapper = wrapper;
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

        public void clearWrapper() { this.wrapper = null; }

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