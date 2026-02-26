package sk.aquaville.modulorum;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import sk.aquaville.modulorum.abstraction.PluginCheckPort;
import sk.aquaville.modulorum.annotation.dependency.DependencyType;
import sk.aquaville.modulorum.annotation.dependency.holder.DependencyMetadataHolder;
import sk.aquaville.modulorum.annotation.holder.ModuleMetadataHolder;
import sk.aquaville.modulorum.annotation.parser.ModuleClassParser;
import sk.aquaville.modulorum.base.AbstractModule;
import sk.aquaville.modulorum.container.LoadedModule;
import sk.aquaville.modulorum.container.ModuleServiceContainer;
import sk.aquaville.modulorum.util.InstanceInvoker;

import java.util.*;

/**
 * Central manager for loading, unloading, and managing modules in the system.
 * <p>
 * The {@code ModuleManager} handles module metadata registration, dependency resolution,
 * instantiation, and lifecycle management (onEnable/onDisable). It supports
 * optional and required module/plugin dependencies, detects cycles in dependencies,
 * and provides reload functionality for reloadable modules.
 * <br>
 * All operations are thread-safe via synchronized methods.
 * </p>
 */
public final class ModuleManager {

    private final ModuleServiceContainer container = new ModuleServiceContainer();
    private final ModuleClassParser moduleParser = new ModuleClassParser();

    private final Logger loggerInstance;
    private final PluginCheckPort pluginCheckPort;

    public ModuleManager(PluginCheckPort pluginChek, Logger logger) {
        pluginCheckPort = pluginChek;
        loggerInstance = logger;
    }

    /**
     * Loads a module class into the system, resolving dependencies and instantiating it.
     * <p>
     * This method performs a depth-first load of dependencies, ensuring that all
     * required modules and plugins are present before enabling the module.
     * Optional dependencies that are missing are skipped with a warning.
     * </p>
     * @param moduleClass the class of the module to load
     */
    public synchronized void load(Class<? extends AbstractModule> moduleClass) {
        ModuleMetadataHolder meta = moduleParser.parse(moduleClass);
        String id = meta.id();

        if (container.contains(id) && container.get(id).instance() != null) {
            throw new IllegalStateException("Module already loaded: " + id);
        }

        // DFS load with cycle detection
        Deque<String> stack = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        dfsLoad(meta, stack, visited);
    }

    /**
     * Performs a depth-first search to load a module and its dependencies.
     * <p>
     * Detects cycles in dependencies and reports them.
     * Ensures that all dependent modules are loaded before the current module.
     * Instantiates the module if it has not been instantiated yet and calls its {@link AbstractModule#onEnable()} method.
     * </p>
     *
     * @param meta the metadata of the module to load
     * @param stack a stack used to detect cycles in the dependency graph
     * @param visited a set of already loaded module IDs
     */
    private void dfsLoad(ModuleMetadataHolder meta, Deque<String> stack, Set<String> visited) {
        String id = meta.id();
        if (stack.contains(id)) {
            // build cycle path for diagnostics
            var cycle = new ArrayList<>(stack);
            cycle.add(id);

            throw new IllegalStateException("Cycle detected in module dependencies: " + String.join(" -> ", cycle));
        }
        if (visited.contains(id) && container.contains(id) && container.get(id).instance() != null) {
            return; // already loaded completely
        }

        stack.push(id);

        for (DependencyMetadataHolder dependency : meta.dependencyMetadataHolder()) {
            boolean optional = dependency.optional();
            switch (dependency.type()) {

                case MODULE -> {
                    String depId = dependency.value();

                    // Metadata not registered → dependency module not found
                    if (!container.contains(depId)) {
                        if (optional) {
                            loggerInstance.warn("Optional module dependency '{}' for module '{}' not found. Skipping.", depId, id);
                            continue; // skip optional
                        }

                        // When required dependency is missing just return and stop loading process
                        loggerInstance.warn("Missing required module metadata for '{}' required by '{}'", depId, id);
                        return;
                    }

                    var depMeta = container.get(depId).metadata();
                    dfsLoad(depMeta, stack, visited);
                }

                case PLUGIN -> {
                    String pluginId = dependency.value();

                    if (!pluginCheckPort.isPluginLoaded(pluginId)) {
                        if (optional) {
                            loggerInstance.warn("Optional plugin dependency '{}' for module '{}' not installed. Skipping.", pluginId, id);
                            continue; // skip optional
                        }

                        // When required dependency is missing just return and stop loading process
                        loggerInstance.warn("Module '{}' requires plugin '{}'", id, pluginId);
                        return;
                    }
                }
            }
        }

        // ensure metadata registered
        if (!container.contains(id)) {
            container.putMetadata(new LoadedModule(id, meta, null));
        }

        // instantiate if not already
        var loaded = container.get(id);
        if (loaded.instance() == null) {
            AbstractModule instance = InstanceInvoker.newInstance(meta.moduleClass());
            if (instance == null) {
                stack.pop();

                throw new IllegalStateException("Could not instantiate module class for: " + id);
            }
            container.setInstance(id, instance);
            // call lifecycle
            instance.onEnable();
            loggerInstance.info("Module {} enabled.", id);
        }

        stack.pop();
        visited.add(id);
    }

    /**
     * Retrieves the module ID for a dependency, ensuring its metadata is registered.
     *
     * @param dependency the dependency to retrieve
     * @param id the ID of the module that requires this dependency
     * @return the dependency module ID, or null if not found (for optional handling)
     */
    private String getString(DependencyMetadataHolder dependency, String id) {
        String depId = dependency.value();
        // If dependency metadata isn't present, parse and register it if available as a class.
        if (!container.contains(depId)) {
            // We try to find metadata by class only if the module is on the classpath.
            // The ModuleClassParser may need your module class; here we assume metadata is known
            // by scanning, or you previously registered metadata. If not found, we error.

            throw new IllegalStateException("Missing required module metadata for '" + depId + "' required by '" + id + "'");
        }
        return depId;
    }

    /**
     * Unloads a module by its ID.
     * <p>
     * Calls {@link AbstractModule#onDisable()} if the module instance exists,
     * and removes the module from the container. Prevents unloading modules
     * that other loaded modules depend on and reports an error if attempted.
     * </p>
     * @param id the ID of the module to unload
     */
    public synchronized void unload(String id) {
        if (!container.contains(id)) {
            return;
        }

        // Prevent unloading a module that other loaded modules depend on:
        for (LoadedModule lm : container.snapshot().values()) {
            if (lm.instance() == null) continue;
            for (DependencyMetadataHolder dep : lm.metadata().dependencyMetadataHolder()) {
                if (dep.type() == DependencyType.MODULE && dep.value().equals(id)) {

                    throw new IllegalStateException("Cannot unload module '" + id + "' because '" + lm.id() + "' depends on it.");
                }
            }
        }

        var removed = container.remove(id);
        if (removed != null && removed.instance() != null) {
            try {
                removed.instance().onDisable();
                loggerInstance.info("Module {} disabled.", id);
            } catch (Exception ex) {
                loggerInstance.error("Exception while disabling module {}: {}", id, ex.getMessage());
            }
        }
    }

    /**
     * Reloads a module by its ID.
     * <p>
     * Equivalent to {@link #unload(String)} followed by {@link #load(Class)}.
     * Only modules marked as reloadable in their metadata are allowed to be reloaded.
     * </p>
     * @param id the ID of the module to reload
     */
    public synchronized void reload(String id) {
        var lm = container.get(id);
        if (lm == null) {
            throw new IllegalStateException("Module not known: " + id);
        }
        if (!lm.metadata().reloadable()) {
            throw new IllegalStateException("Module is not reloadable: " + id);
        }
        Class<? extends AbstractModule> clazz = lm.metadata().moduleClass();
        unload(id);
        load(clazz);
        loggerInstance.info("Module {} reloaded.", id);
    }

    /**
     * Unloads all modules in reverse dependency order.
     * <p>
     * Constructs a dependency graph for loaded modules and uses a topological sort
     * to determine a safe unload order. Modules are unloaded in an order that ensures
     * dependents are disabled before the modules they rely on.
     * In case of a cycle in dependencies, falls back to best-effort unload order.
     * </p>
     */
    public synchronized void unloadAll() {
        var snapshot = container.snapshot();
        // Build dependency graph (only for currently loaded modules)
        Map<String, Set<String>> graph = new HashMap<>();
        for (var entry : snapshot.entrySet()) {
            String id = entry.getKey();
            graph.putIfAbsent(id, new HashSet<>());
            var meta = entry.getValue().metadata();
            for (DependencyMetadataHolder dep : meta.dependencyMetadataHolder()) {
                if (dep.type() == DependencyType.MODULE) {
                    graph.computeIfAbsent(dep.value(), k -> new HashSet<>());
                    // edge dep -> id (dep must be unloaded after dependents)
                    graph.get(dep.value()).add(id);
                }
            }
        }

        // Kahn's algorithm for topological order, then reverse for unload
        Map<String, Integer> inDegree = new HashMap<>();
        for (String k : graph.keySet()) inDegree.put(k, 0);
        for (var adj : graph.values()) {
            for (String to : adj) inDegree.merge(to, 1, Integer::sum);
        }

        Deque<String> q = new ArrayDeque<>();
        for (var e : inDegree.entrySet()) if (e.getValue() == 0) q.add(e.getKey());

        List<String> topo = new ArrayList<>();
        while (!q.isEmpty()) {
            String cur = q.remove();
            topo.add(cur);
            for (String neighbor : graph.getOrDefault(cur, new HashSet<>())) {
                inDegree.merge(neighbor, -1, Integer::sum);
                if (inDegree.get(neighbor) == 0) q.add(neighbor);
            }
        }

        // If topo does not contain all nodes, cycle exists in metadata; fallback to simply unload order
        List<String> unloadOrder;
        if (topo.size() == graph.size()) {
            Collections.reverse(topo); // unload dependents first
            unloadOrder = topo;
        } else {
            unloadOrder = new ArrayList<>(snapshot.keySet());
        }

        for (String id : unloadOrder) {
            try {
                unload(id);
            } catch (Exception ex) {
                loggerInstance.error("Failed to unload module {}: {}", id, ex.getMessage());
            }
        }
    }

    /**
     * Checks if a module with the given ID is loaded and instantiated.
     *
     * @param id the ID of the module
     * @return true if the module exists and has been instantiated, false otherwise
     */
    public boolean isLoaded(String id) {
        var lm = container.get(id);
        return lm != null && lm.instance() != null;
    }

    /**
     * Returns a set of all module IDs currently registered in the container.
     *
     * @return a {@link Set} of module IDs
     */
    public Set<String> getAllIds() {
        return container.ids();
    }

    /**
     * Retrieves the runtime instance of a loaded module.
     *
     * @param id the ID of the module
     * @return the {@link AbstractModule} instance, or null if the module is not loaded
     */
    @Nullable
    public AbstractModule getModule(String id) {
        var lm = container.get(id);
        return lm == null ? null : lm.instance();
    }

    /**
     * Retrieves the metadata of a loaded module.
     *
     * @param id the ID of the module
     * @return the {@link ModuleMetadataHolder} containing module metadata,
     *         or null if the module is not loaded
     */
    @Nullable
    public ModuleMetadataHolder getMetadata(String id) {
        var lm = container.get(id);
        return lm == null ? null : lm.metadata();
    }

}
