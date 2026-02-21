package sk.aquaville.modulorum.annotation.dependency;

/**
 * Defines the type of dependency for a module in the system.
 * <p>
 * {@code DependencyType} is used in the {@link Dependency} annotation
 * to indicate what kind of dependency a module has. This helps the
 * module loader determine how to resolve and enforce dependencies
 * during module loading.
 * </p>
 */
public enum DependencyType {

    /**
     * Indicates that the dependency is another module in the system.
     * The module loader will ensure that the target module is loaded
     * and available before loading the dependent module.
     */
    MODULE,

    /**
     * Indicates that the dependency is an external plugin.
     * The module loader will check that the required plugin is present
     * in the runtime environment before loading the dependent module.
     */
    PLUGIN,
}
