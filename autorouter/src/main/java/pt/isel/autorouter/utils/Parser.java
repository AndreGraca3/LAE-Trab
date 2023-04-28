package pt.isel.autorouter.utils;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class Parser {

    public static Object parse(String name, Class<?> type, Map<String, String> args) throws Exception {

        if (type.isPrimitive() || type == String.class) return parseObject(type, args.get(name)); // "19" => 19

        /** From this point we know it is a complex Object **/
        if (type.getDeclaredConstructors().length != 1) throw new NoSuchMethodException();

        Constructor<?> c = type.getDeclaredConstructors()[0];

        Object[] cArgs = Arrays.stream(c.getParameters()).map(p -> {
            try {
                return parseObject(p.getType(), args.get(p.getName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toArray(Object[]::new);

        return c.newInstance(cArgs);
    }


    private static Object parseObject(Class<?> clazz, String value) {
        String cName = clazz.getName();

        if (clazz == String.class) return value;

        if (!clazz.isPrimitive())
            throw new IllegalArgumentException("Cannot convert non-primitive type: " + cName);

        return primitiveToWrapper.get(clazz).apply(value);
    }

    public static final Map<Class<?>, Function<String, Object>> primitiveToWrapper = Map.of(
            int.class, Integer::parseInt,
            long.class, Long::parseLong,
            float.class, Float::parseFloat,
            double.class, Double::parseDouble,
            short.class, Short::parseShort,
            byte.class, Byte::parseByte,
            boolean.class, Boolean::parseBoolean);
}
