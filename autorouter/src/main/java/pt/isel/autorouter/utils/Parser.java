package pt.isel.autorouter.utils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Parser {

    public static <T> Object parse(String name, Class<?> type, Map<String, String> args) throws Exception {

        if (type.isPrimitive() || type == String.class) return parseObject(type, args.get(name)); // "19" => 19

        /** From this point we know it is a complex Object **/
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

        return primitiveToWrapper.get(clazz).apply(value);
    }

        /*private static Method getParserMethod (Class < ? > clazz){
            return Arrays.stream(primitiveToWrapper.get(clazz).getMethods()).filter(it -> it.getName().toLowerCase().equals("parse" + clazz.getName()) && it.getParameterTypes().length == 1).toArray(Method[]::new)[0];
        }*/

        private static final Map<Class<?>, Function<String, Object>> primitiveToWrapper = Map.of(
                int.class, Integer::parseInt,
                long.class, Long::parseLong,
                float.class, Float::parseFloat,
                double.class, Double::parseDouble,
                short.class, Short::parseShort,
                byte.class, Byte::parseByte,
                boolean.class, Boolean::parseBoolean);
    }
