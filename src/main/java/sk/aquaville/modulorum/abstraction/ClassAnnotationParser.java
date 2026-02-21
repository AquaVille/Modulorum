package sk.aquaville.modulorum.abstraction;

/**
 * Parses class-level metadata and produces a result based on annotations
 * or other declarative information.
 * <p>
 * {@code ClassAnnotationParser} defines a contract for extracting and
 * interpreting annotations from a target class (or class-like structure)
 * and converting them into a domain-specific representation.
 * </p>
 *
 * @param <T> the type of the parsing result produced by this parser
 * @param <V> the type representing the target class to be parsed
 *            (e.g. {@link Class}, or a framework-specific abstraction)
 */
public interface ClassAnnotationParser<T, V> {

    /**
     * Parses the given target class and returns the extracted representation.
     * <p>
     * Implementations are responsible for inspecting annotations and
     * applying any required validation or transformation logic.
     * </p>
     *
     * @param targetClass the class or class-like object to be parsed
     * @return the result produced from parsing the target class metadata
     */
    T parse(V targetClass);

}
