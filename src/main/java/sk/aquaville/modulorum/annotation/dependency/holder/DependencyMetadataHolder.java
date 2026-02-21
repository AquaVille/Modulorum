package sk.aquaville.modulorum.annotation.dependency.holder;

import com.github.bsideup.jabel.Desugar;
import sk.aquaville.modulorum.annotation.dependency.Dependency;
import sk.aquaville.modulorum.annotation.dependency.DependencyType;

/**
 * Holds metadata for a module dependency.
 * <p>
 * {@code DependencyMetadataHolder} is a simple, immutable container
 * for storing information about a module's dependency, typically
 * extracted from the {@link Dependency} annotation.
 * <br>
 * This record is used by the module loader to manage dependencies,
 * check for required modules or plugins, and enforce optional/mandatory
 * dependency rules during module loading and initialization.
 * </p>
 *
 * @param type     the type of the dependency, as defined by {@link DependencyType}
 * @param value    the identifier of the dependent module or plugin
 * @param optional {@code true} if the dependency is optional, {@code false} if it is mandatory
 */
@Desugar
public record DependencyMetadataHolder(DependencyType type, String value, boolean optional) {}
