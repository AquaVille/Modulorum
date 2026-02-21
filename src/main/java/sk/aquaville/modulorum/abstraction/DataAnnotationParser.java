package sk.aquaville.modulorum.abstraction;

/**
 * Parses annotation instances and converts them into a domain-specific
 * representation.
 * <p>
 * {@code DataAnnotationParser} defines a contract for interpreting the
 * values and attributes of an annotation and transforming them into a
 * usable data model. It is typically used after annotations have been
 * discovered on classes, methods, or fields.
 * </p>
 *
 * @param <T> the type of the parsing result produced by this parser
 * @param <V> the type of the annotation to be parsed
 *            (e.g. {@link java.lang.annotation.Annotation} or a specific annotation type)
 */
public interface DataAnnotationParser<T, V> {

    /**
     * Parses the given annotation instance.
     * <p>
     * Implementations are responsible for extracting annotation attributes
     * and applying any necessary validation or transformation logic.
     * </p>
     *
     * @param annotation the annotation instance to be parsed
     * @return the result produced from parsing the annotation data
     */
    T parse(V annotation);
}
