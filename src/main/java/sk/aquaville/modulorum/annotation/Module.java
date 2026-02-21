package sk.aquaville.modulorum.annotation;

import sk.aquaville.modulorum.annotation.dependency.Dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a module in the system.
 * <p>
 * {@code @Module} is used to provide metadata about a module, such as
 * its unique identifier, version, authorship, description, and dependencies.
 * Modules annotated with this can be discovered, loaded, and managed
 * dynamically by the module system.
 * <br>
 * Modules may optionally be reloadable at runtime depending on the
 * {@link #reloadable()} flag.
 * </p>
 *
 * Example:
 * {@code
 * @Module(
 *     id = "exampleModule",
 *     description = "An example module",
 *     authors = {"Alice", "Bob"},
 *     version = "1.2.0",
 *     dependency = {@Dependency(id = "core")},
 *     reloadable = true
 * )
 * public class ExampleModule {
 *     // Module implementation
 * }
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {

    /**
     * The unique identifier for the module.
     *
     * @return the module ID
     */
    String id();

    /**
     * A short description of the module.
     *
     * @return the module description
     */
    String description() default "";

    /**
     * The authors of the module.
     *
     * @return an array of author names
     */
    String[] authors() default {};

    /**
     * The version of the module.
     *
     * @return the module version
     */
    String version() default "1.0.0";

    /**
     * The dependencies of the module.
     * Each dependency must be satisfied for the module to load correctly.
     *
     * @return an array of {@link Dependency} annotations
     */
    Dependency[] dependency() default {};

    /**
     * Indicates whether the module can be reloaded at runtime.
     *
     * @return {@code true} if reloadable; {@code false} otherwise
     */
    boolean reloadable() default true;
}
