package sk.aquaville.modulorum.container;

import sk.aquaville.modulorum.base.AbstractModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Container and registry for loaded modules in the system.
 * <p>
 * {@code ModuleServiceContainer} manages all module metadata and runtime instances,
 * providing thread-safe operations for registering, updating, retrieving, and removing modules.
 * It acts as the central storage for the module system to track loaded modules and their state.
 * </p>
 * Modules are tracked by their unique ID, and this container ensures that no duplicate
 * module metadata is registered. Module instances can be updated separately using {@link #setInstance(String, AbstractModule)}.
 * 
 */
public final class ModuleServiceContainer {

    private final Map<String, LoadedModule> modules = new ConcurrentHashMap<>();

    /**
     * Checks if a module with the given ID is present in the container.
     *
     * @param id the unique identifier of the module
     * @return {@code true} if a module with the given ID exists; {@code false} otherwise
     */
    public boolean contains(String id) {
        return modules.containsKey(id);
    }

    /**
     * Retrieves the loaded module associated with the given ID.
     *
     * @param id the unique identifier of the module
     * @return the {@link LoadedModule} if found, or {@code null} if no module is registered with the ID
     */
    public LoadedModule get(String id) {
        return modules.get(id);
    }

    /**
     * Registers module metadata in the container.
     * <p>
     * The module metadata is added only if no module with the same ID is already registered.
     * Attempting to register a module that already exists will throw an {@link IllegalStateException}.
     * </p>
     *
     * @param loaded the {@link LoadedModule} containing metadata to register
     * @throws IllegalStateException if a module with the same ID is already registered
     */
    public void putMetadata(LoadedModule loaded) {
        var prev = modules.putIfAbsent(loaded.id(), loaded);
        if (prev != null) {
            throw new IllegalStateException("Module already registered: " + loaded.id());
        }
    }

    /**
     * Updates the runtime instance of a module.
     * <p>
     * This replaces the {@link AbstractModule} instance for the module with the given ID.
     * The module metadata must already be registered; otherwise, an exception is thrown.
     * </p>
     *
     * @param id       the unique identifier of the module
     * @param instance the new {@link AbstractModule} instance
     * @return the updated {@link LoadedModule} containing the new instance
     * @throws IllegalStateException if no metadata is registered for the module ID
     */
    public LoadedModule setInstance(String id, AbstractModule instance) {
        return modules.compute(id, (k, old) -> {
            if (old == null) {
                throw new IllegalStateException("No metadata registered for module: " + id);
            }
            return old.withInstance(instance);
        });
    }

    /**
     * Removes a module from the container.
     *
     * @param id the unique identifier of the module
     * @return the removed {@link LoadedModule}, or {@code null} if no module was registered with the ID
     */
    public LoadedModule remove(String id) {
        return modules.remove(id);
    }

    /**
     * Returns a set of all module IDs currently registered in the container.
     *
     * @return a {@link Set} of module IDs
     */
    public Set<String> ids() {
        return modules.keySet();
    }

    /**
     * Returns a snapshot of all loaded modules.
     * <p>
     * The returned map is an unmodifiable copy of the current container state.
     * </p>
     * @return a {@link Map} of module IDs to {@link LoadedModule} objects
     */
    public Map<String, LoadedModule> snapshot() {
        return new HashMap<>(modules);
    }
}
