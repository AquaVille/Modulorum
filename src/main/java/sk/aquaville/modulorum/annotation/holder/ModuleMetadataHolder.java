package sk.aquaville.modulorum.annotation.holder;

import com.github.bsideup.jabel.Desugar;
import sk.aquaville.modulorum.annotation.dependency.holder.DependencyMetadataHolder;
import sk.aquaville.modulorum.base.AbstractModule;

import java.util.List;

/**
 * Holds metadata for a module in the system.
 * <p>
 * {@code ModuleMetadataHolder} is a container for all metadata extracted
 * from a class annotated with {@link Module}. It provides structured
 * access to the module's ID, description, authors, version, reloadability,
 * dependencies, and the actual module class.
 * <br>
 * This record is typically used internally by the module loader or manager
 * to track modules, resolve dependencies, and handle dynamic loading
 * and reloading.
 * </p>
 *
 * @param id                        the unique identifier of the module
 * @param description               a short description of the module
 * @param authors                   the list of authors of the module
 * @param version                   the version string of the module
 * @param reloadable                {@code true} if the module can be reloaded at runtime; {@code false} otherwise
 * @param dependencyMetadataHolder  a list of {@link DependencyMetadataHolder} representing module dependencies
 * @param moduleClass               the class implementing the module, extending {@link AbstractModule}
 */
@Desugar
public record ModuleMetadataHolder(
        String id,
        String description,
        List<String> authors,
        String version,
        Boolean reloadable,
        List<DependencyMetadataHolder> dependencyMetadataHolder,
        Class<? extends AbstractModule> moduleClass
) {
}
