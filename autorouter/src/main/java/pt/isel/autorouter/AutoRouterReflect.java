package pt.isel.autorouter;

import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;
import pt.isel.autorouter.utils.Parser;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class AutoRouterReflect {

    public static Stream<ArHttpRoute> autorouterReflect(Object controller) {

        Class<?> controllerClass = controller.getClass();

        Stream<Method> methods = Arrays.stream(controllerClass.getDeclaredMethods());

        // For each method check: annotated with @AutoRoute, returns Optional and create ArHttpRoute
        return methods.filter(m -> m.isAnnotationPresent(AutoRoute.class) && m.getReturnType() == Optional.class)
                .map(m -> createArHttpRoute(controller, m.getAnnotation(AutoRoute.class), m));
    }


    private static ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method) {

        Parameter[] params = method.getParameters();

        //Array with each param annotation
        Class<?>[] paramsAnnotations = Arrays.stream(params).map(
                p -> findAnnotation(p, ArRoute.class, ArQuery.class, ArBody.class)
        ).toArray(Class[]::new);

        Map<Class<?>, Map<String, String>> annotationsMap = new HashMap<>(Map.of(
                ArRoute.class, new HashMap<>(),
                ArQuery.class, new HashMap<>(),
                ArBody.class, new HashMap<>()));

        ArHttpHandler handler = (routeArgs, queryArgs, bodyArgs) -> {

            if (routeArgs != null) {
                annotationsMap.put(ArRoute.class, routeArgs);
            }
            if (queryArgs != null) {
                annotationsMap.put(ArQuery.class, queryArgs);
            }
            if (bodyArgs != null) {
                annotationsMap.put(ArBody.class, bodyArgs);
            }

            //for each param, get its annotation and use it to get param value from corresponding map
            Stream<Object> args = IntStream.range(0, params.length)
                    .mapToObj(i -> {
                        Parameter param = params[i];
                        String pName = param.getName();
                        Class<?> pType = param.getType();
                        try {
                            return Parser.parse(pName, pType, annotationsMap.get(paramsAnnotations[i]));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            try {
                return (Optional<?>) method.invoke(controller, args.toArray());  // Handler invokes the method and returns its results
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return new ArHttpRoute(method.getName(), annotation.method(), annotation.path(), handler);
    }

    private static Class<?> findAnnotation(Parameter p, Class<?>... annotations) {
        return Arrays.stream(annotations).filter(
                a -> p.isAnnotationPresent((Class<? extends Annotation>) a)
        ).findFirst().orElseThrow(() -> new InvalidParameterException("Missing Annotation in Parameter: " + p.getName()));
    }
}