package sk.aquaville.modulorum.container;

import com.github.bsideup.jabel.Desugar;
import sk.aquaville.modulorum.annotation.holder.ModuleMetadataHolder;
import sk.aquaville.modulorum.base.AbstractModule;

/**
 * Represents a module that has been loaded into the system.
 * <p>
 * {@code LoadedModule} contains the module's unique identifier, its metadata,
 * and the actual module instance. It is typically used by the module loader
 * or manager to keep track of active modules and their runtime state.
 * <br>
 * The record is immutable, but a new instance with a different {@link AbstractModule}
 * instance can be created using the {@link #withInstance(AbstractModule)} method.
 * </p>
 *
 * @param id       the unique identifier of the module
 * @param metadata the metadata of the module, as represented by {@link ModuleMetadataHolder}
 * @param instance the running instance of the module
 */
@Desugar
public record LoadedModule(
        String id,
        ModuleMetadataHolder metadata,
        AbstractModule instance
) {

    /**
     * Returns a copy of this {@code LoadedModule} with a new module instance.
     * <p>
     * This is useful for updating the runtime instance of a module without
     * modifying the original record.
     * </p>
     *
     * @param instance the new module instance
     * @return a new {@code LoadedModule} containing the same ID and metadata but the new instance
     */
    public LoadedModule withInstance(AbstractModule instance) {
        return new LoadedModule(this.id, this.metadata, instance);
    }
}
