package sk.aquaville.modulorum.base;

/**
 * Base class for all modules in the system.
 * <p>
 * {@code AbstractModule} defines the lifecycle methods that every module
 * must implement to handle initialization and cleanup. Module classes
 * should extend this abstract class and provide concrete behavior for
 * enabling and disabling the module.
 * <br>
 * Modules are typically loaded, managed, and executed by a module system
 * that recognizes classes annotated with {@link Module} and extending
 * this class.
 * </p>
 */
public abstract class AbstractModule {

    /**
     * Called when the module is being enabled or loaded.
     * Implementations should include initialization logic, such as
     * registering services, listeners, or starting timers.
     */
    public abstract void onEnable();

    /**
     * Called when the module is being disabled or unloaded.
     * Implementations should include cleanup logic, such as unregistering
     * services, listeners, or stopping timers.
     */
    public abstract void onDisable();
}
