package sk.aquaville.modulorum.annotation.dependency.parser;

import sk.aquaville.modulorum.abstraction.DataAnnotationParser;
import sk.aquaville.modulorum.annotation.dependency.holder.DependencyMetadataHolder;
import sk.aquaville.modulorum.annotation.dependency.Dependency;

/**
 * Parses {@link Dependency} annotations into {@link DependencyMetadataHolder} objects.
 * <p>
 * {@code ModuleDependencyAnnotationParser} implements {@link DataAnnotationParser}
 * to convert module dependency annotations into a structured, easily consumable
 * form that can be used by the module loader to manage and enforce dependencies.
 * <br>
 * Each {@link Dependency} annotation on a module is transformed into a
 * {@link DependencyMetadataHolder}, preserving the dependency type,
 * identifier, and optional flag.
 * <br>
 * This parser is typically used internally by the module system when
 * scanning modules annotated with {@link Module} to build metadata objects
 * for loading and dependency resolution.
 * </p>
 */
public class ModuleDependencyAnnotationParser implements DataAnnotationParser<DependencyMetadataHolder, Dependency> {

    /**
     * Converts a {@link Dependency} annotation into a {@link DependencyMetadataHolder}.
     *
     * @param annotation the dependency annotation to parse
     * @return a {@link DependencyMetadataHolder} containing the dependency type, value, and optional flag
     */
    @Override
    public DependencyMetadataHolder parse(Dependency annotation) {
        return new DependencyMetadataHolder(annotation.type(), annotation.value(), annotation.optional());
    }
}
