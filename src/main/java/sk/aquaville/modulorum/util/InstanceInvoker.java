package sk.aquaville.modulorum.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

@SuppressWarnings({"unchecked", "unused"})
@UtilityClass
public class InstanceInvoker {

    /**
     * Creates a new instance of a class using reflection.
     *
     * @param clazz The class to instantiate.
     * @param args The arguments to pass to the constructor.
     * @return A new instance of the class, or {@code null} if the instantiation fails.
     */
    @Nullable
    public static <T> T newInstance(@NotNull Class<T> clazz, @Nullable Object... args) {
        Object[] realArgs = args == null ? new Object[0] : args;
        try {
            for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
                if (matchesExecutable(ctor.getParameterTypes(), realArgs)) {
                    ctor.setAccessible(true);
                    return (T) ctor.newInstance(realArgs);
                }
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Checks if the given parameter types match the types of the provided arguments.
     *
     * @param paramTypes The parameter types of the constructor or method.
     * @param args The arguments to check.
     * @return {@code true} if the parameter types match the argument types, {@code false} otherwise.
     */
    private static boolean matchesExecutable(@NotNull Class<?>[] paramTypes, @NotNull Object[] args) {
        if (paramTypes.length != args.length) return false;

        for (int i = 0; i < paramTypes.length; i++) {
            Object arg = args[i];
            Class<?> param = wrapper(paramTypes[i]);

            if (arg == null) {
                if (param.isPrimitive()) return false;
            } else {
                if (!param.isAssignableFrom(arg.getClass())) return false;
            }
        }
        return true;
    }

    /**
     * Converts primitive types to their corresponding wrapper types.
     *
     * @param c The class of the primitive type.
     * @return The wrapper class corresponding to the primitive type, or the original class if it is not primitive.
     */
    private static Class<?> wrapper(Class<?> c) {
        if (!c.isPrimitive()) return c;
        return switch (c.getName()) {
            case "int" -> Integer.class;
            case "long" -> Long.class;
            case "boolean" -> Boolean.class;
            case "byte" -> Byte.class;
            case "short" -> Short.class;
            case "char" -> Character.class;
            case "float" -> Float.class;
            case "double" -> Double.class;
            default -> c;
        };
    }
}
