package sk.aquaville.modulorum.abstraction;

/**
 * Interface for checking the presence of plugins in a system.
 * <p>
 * {@code PluginCheckPort} provides a simple abstraction to determine whether
 * a plugin, identified by a unique string identifier, is currently loaded
 * or available in the runtime environment.
 * <br>
 * Implementations may vary depending on the underlying plugin system,
 * framework, or platform.
 * </p>
 */
public interface PluginCheckPort {

    /**
     * Checks whether a plugin with the given identifier is loaded.
     *
     * @param identifier the unique identifier of the plugin (e.g., name, key)
     * @return {@code true} if the plugin is loaded and available; {@code false} otherwise
     */
    boolean isPluginLoaded(String identifier);
}
