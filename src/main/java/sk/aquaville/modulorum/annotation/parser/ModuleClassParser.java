package sk.aquaville.modulorum.annotation.parser;

import sk.aquaville.modulorum.annotation.Module;
import sk.aquaville.modulorum.annotation.dependency.holder.DependencyMetadataHolder;
import sk.aquaville.modulorum.annotation.dependency.Dependency;
import sk.aquaville.modulorum.annotation.dependency.parser.ModuleDependencyAnnotationParser;
import sk.aquaville.modulorum.base.AbstractModule;
import sk.aquaville.modulorum.abstraction.ClassAnnotationParser;
import sk.aquaville.modulorum.annotation.holder.ModuleMetadataHolder;
import sk.aquaville.modulorum.exception.InvalidClassAnnotationException;
import sk.aquaville.modulorum.exception.MissingAnnotationException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Parses module classes annotated with {@link Module} into {@link ModuleMetadataHolder} objects.
 * <p>
 * {@code ModuleClassParser} implements {@link ClassAnnotationParser} to extract
 * all relevant metadata from a class extending {@link AbstractModule}. This includes
 * the module's ID, description, authors, version, reloadability, and dependencies.
 * <br>
 * Dependencies are parsed using {@link ModuleDependencyAnnotationParser} to produce
 * a list of {@link DependencyMetadataHolder} objects for easier management by the module loader.
 * <br>
 * This parser performs validation to ensure that:
 * <ul>
 *     <li>The class is annotated with {@link Module}.</li>
 *     <li>The class extends {@link AbstractModule}.</li>
 * </ul>
 * If these checks fail, custom exceptions are thrown:
 * {@link MissingAnnotationException} or {@link InvalidClassAnnotationException}.
 * </p>
 */
public class ModuleClassParser implements ClassAnnotationParser<ModuleMetadataHolder, Class<? extends AbstractModule>> {

    ModuleDependencyAnnotationParser dependencyParser = new ModuleDependencyAnnotationParser();


    /**
     * Parses the given module class into a {@link ModuleMetadataHolder}.
     * <p>
     * Extracts module metadata, including dependencies, authors, version, and reloadability.
     * Validates that the class is properly annotated and extends {@link AbstractModule}.
     * </p>
     *
     * @param moduleClass the class of the module to parse
     * @return a {@link ModuleMetadataHolder} containing metadata for the module
     * @throws MissingAnnotationException       if the module class is not annotated with {@link Module}
     * @throws InvalidClassAnnotationException if the module class does not extend {@link AbstractModule}
     */
    @Override
    public ModuleMetadataHolder parse(Class<? extends AbstractModule> moduleClass) {
        if (!moduleClass.isAnnotationPresent(Module.class)) {
            throw new MissingAnnotationException("Module class " + moduleClass.getName() + " is missing required @Module annotation.");
        }
        if (!(moduleClass instanceof Class<? extends AbstractModule>)) {
            throw new InvalidClassAnnotationException("Required module class " + moduleClass.getName() + " is not extending AbstractModule class.");
        }
        Module module = moduleClass.getAnnotation(Module.class);

        return getModuleMetadata(module, moduleClass);
    }

    /**
     * Converts a {@link Module} annotation and its class into a {@link ModuleMetadataHolder}.
     *
     * @param module      the module annotation
     * @param moduleClass the class implementing the module
     * @return a {@link ModuleMetadataHolder} containing parsed metadata
     */
    private ModuleMetadataHolder getModuleMetadata(Module module, Class<? extends AbstractModule> moduleClass) {
        var dependencies = new ArrayList<DependencyMetadataHolder>();
        for (Dependency dependency : module.dependency()) {
            dependencies.add(dependencyParser.parse(dependency));
        }
        var authors = new ArrayList<>(Arrays.asList(module.authors()));

        return new ModuleMetadataHolder(module.id(), module.description(), authors, module.version(), module.reloadable(), dependencies, moduleClass);
    }

}
