package sk.aquaville.modulorum.exception;

import sk.aquaville.modulorum.annotation.parser.ModuleClassParser;

/**
 * Thrown when a required annotation is missing from a class in the module system.
 * <p>
 * This exception is typically used by the {@link ModuleClassParser} when
 * a class that is expected to be annotated with {@link Module} is not,
 * indicating that it cannot be properly processed as a module.
 * </p>
 */
public class MissingAnnotationException extends IllegalArgumentException {

    /**
     * Constructs a new {@code MissingAnnotationException} with the specified detail message.
     *
     * @param message the detail message explaining which annotation is missing
     */
    public MissingAnnotationException(String message) {
        super(message);
    }
}
