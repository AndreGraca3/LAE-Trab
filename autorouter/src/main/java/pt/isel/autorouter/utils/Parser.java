package pt.isel.autorouter.utils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Parser {

    public static <T> Object parse(String name, Class<?> type, Map<String, String> args) throws Exception {

        if (type.isPrimitive() || type == String.class) return parseObject(type, args.get(name)); // "19" => 19

        /** From this point we know it is a complex Object */
        if (type.getConstructors().length != 1) throw new NoSuchMethodException();

        // Get Constructor's parameters' types
        Class<?>[] paramTypes = Arrays.stream(type.getDeclaredFields()).map(Field::getType).toArray(Class[]::new);

        Constructor<?> c = type.getDeclaredConstructor(paramTypes);

        Object[] cArgs = Arrays.stream(c.getParameters()).map(it -> {
            try {
                return parseObject(it.getType(), args.get(it.getName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toArray(Object[]::new);

        return c.newInstance(cArgs);
    }


    private static Object parseObject(Class<?> clazz, String value) throws Exception {
        String cName = clazz.getName();

        if (clazz == String.class) return value;

        if (!clazz.isPrimitive())
            throw new IllegalArgumentException("Cannot convert non-primitive type: " + cName);

        switch (cName) {
            case "int", "long", "float", "double", "short", "byte", "boolean" -> {
                return getParserMethod(clazz).invoke(null, value);
            }
            case "char" -> {
                if (value.length() != 1)
                    throw new IllegalArgumentException("Cannot convert \"" + value + "\" to char.");
                return value.charAt(0);
            }
            default -> throw new IllegalArgumentException("Invalid primitive type: " + cName);
        }
    }

    private static Method getParserMethod(Class<?> clazz) {
        return Arrays.stream(primitiveToWrapper.get(clazz).getMethods()).filter(it -> it.getName().toLowerCase().equals("parse" + clazz.getName()) && it.getParameterTypes().length == 1).toArray(Method[]::new)[0];
    }

    private static final Map<Class<?>, Class<?>> primitiveToWrapper = Map.of(
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            short.class, Short.class,
            byte.class, Byte.class,
            boolean.class, Boolean.class);
}
