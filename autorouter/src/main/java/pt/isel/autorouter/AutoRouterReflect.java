package pt.isel.autorouter;

import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Stream;


public class AutoRouterReflect {

    public static Stream<ArHttpRoute> autorouterReflect(Object controller) {

        Class<?> controllerClass = controller.getClass();

        Stream<Method> methods = Arrays.stream(controllerClass.getDeclaredMethods());

        // For each method check: annotated with @AutoRoute, returns Optional and create ArHttpRoute
        return methods.filter(m -> m.isAnnotationPresent(AutoRoute.class) && m.getReturnType() == Optional.class)
                .map(m -> createArHttpRoute(controller, m.getAnnotation(AutoRoute.class), m));
    }

    private static ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method)  {

        ArHttpHandler handler = (routeArgs, queryArgs, bodyArgs) -> {

            Stream<Object> args = Arrays.stream(method.getParameters())     //Stream of args for method invoke
                    .map(p -> {
                        String pName = p.getName();
                        Class<?> pType = p.getType();
                        if (p.isAnnotationPresent(ArRoute.class)) {
                            try {
                                return parse(pName,pType,routeArgs);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (p.isAnnotationPresent(ArQuery.class)) return queryArgs.get(pName);
                        if (p.isAnnotationPresent(ArBody.class)) {
                            try {
                                return parse(pName,pType,bodyArgs);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else throw new InvalidParameterException("Missing Annotation in Parameter: " + pName);
                    });
            try {
                return (Optional<?>) method.invoke(controller, args.toArray());  // Handler invokes the method and returns its results
            } catch (Exception e) {     //awaiting confirmation
                throw new RuntimeException(e);
            }
        };
        return new ArHttpRoute(method.getName(), annotation.method(), annotation.path(), handler);
    }

    private static <T> Object parse(String name, Class<?> type, Map<String, String> args) throws Exception { // obj = Player(number=88,name="some name")

        if (type.isPrimitive())
            return parseObject(type,args.get(name)); // "19" => 19

        if(String.class == type)
            return args.get(name);

        if(type.getConstructors().length != 1) throw new NoSuchMethodException();

        Parameter[] p = type.getDeclaredConstructor().getParameters();

        List arr = new ArrayList();

        for (Parameter parameter : p) {
            String currName = parameter.getName(); // "number"
            Class<?> currType = parameter.getType(); // "int"
            String prop_val = args.get(currName); // "19"
            arr.add(parseObject(currType,prop_val));
        }
        return type.getDeclaredConstructor().newInstance(args);
    }


    private static Object parseObject(Class<?> clazz, String value) throws IllegalArgumentException {
        if (clazz.isPrimitive()) {
            switch (clazz.getName()) {
                case "int" -> {
                    return Integer.parseInt(value);
                }
                case "long" -> {
                    return Long.parseLong(value);
                }
                case "float" -> {
                    return Float.parseFloat(value);
                }
                case "double" -> {
                    return Double.parseDouble(value);
                }
                case "short" -> {
                    return Short.parseShort(value);
                }
                case "byte" -> {
                    return Byte.parseByte(value);
                }
                case "boolean" -> {
                    return Boolean.parseBoolean(value);
                }
                case "char" -> {
                    if (value.length() != 1) {
                        throw new IllegalArgumentException("Cannot convert \"" + value + "\" to char.");
                    }
                    return value.charAt(0);
                }
                default -> throw new IllegalArgumentException("Invalid primitive type: " + clazz.getName());
            }
        } else if (clazz == String.class) {
            return value;
        }
        throw new IllegalArgumentException("Cannot convert to non-primitive/non-String type: " + clazz.getName());
    }

}