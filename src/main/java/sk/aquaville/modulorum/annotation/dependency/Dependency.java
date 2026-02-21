package sk.aquaville.modulorum.annotation.dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a dependency for a module in the system.
 * <p>
 * {@code @Dependency} is used within the {@link Module} annotation to
 * specify other modules that must be present for the annotated module
 * to load correctly. Dependencies can be mandatory or optional,
 * and can indicate different types of relationships via {@link DependencyType}.
 * <br>
 * This annotation is intended to be used on other annotations, such as
 * within {@code @Module}, and is processed by the module loader to
 * enforce dependency rules.
 * </p>
 *
 * Example:
 * {@code
 * @Module(
 *     id = "exampleModule",
 *     dependency = {
 *         @Dependency(type = DependencyType.HARD, value = "core"),
 *         @Dependency(type = DependencyType.SOFT, value = "optionalModule", optional = true)
 *     }
 * )
 * public class ExampleModule extends AbstractModule {
 *     // Module implementation
 * }
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Dependency {

    /**
     * The type of the dependency.
     * Determines how strictly the module loader enforces the presence
     * of the target module.
     *
     * @return the {@link DependencyType} of this dependency
     */
    DependencyType type();

    /**
     * The identifier of the module this dependency refers to.
     *
     * @return the module ID this module depends on
     */
    String value();

    /**
     * Whether this dependency is optional.
     * <p>
     * If {@code true}, the module can still load even if this dependency
     * is missing. If {@code false}, the module loader may prevent the module
     * from loading until the dependency is satisfied.
     * </p>
     *
     * @return {@code true} if the dependency is optional; {@code false} otherwise
     */
    boolean optional() default false;
}
