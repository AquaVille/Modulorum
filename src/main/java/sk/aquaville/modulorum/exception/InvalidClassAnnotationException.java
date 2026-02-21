package sk.aquaville.modulorum.exception;

import sk.aquaville.modulorum.annotation.parser.ModuleClassParser;
import sk.aquaville.modulorum.base.AbstractModule;

/**
 * Thrown when a class that is expected to have a specific annotation
 * does not meet the required criteria for the module system.
 * <p>
 * This exception is typically used by the {@link ModuleClassParser} when
 * a class annotated as a module does not extend {@link AbstractModule}
 * or otherwise violates annotation requirements.
 * </p>
 */
public class InvalidClassAnnotationException extends IllegalArgumentException {

    /**
     * Constructs a new {@code InvalidClassAnnotationException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidClassAnnotationException(String message) { super(message); }
}
