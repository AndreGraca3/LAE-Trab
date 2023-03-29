package pt.isel.autorouter;

import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;
import pt.isel.autorouter.utils.Parser;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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


    private static ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method) {

        ArHttpHandler handler = (routeArgs, queryArgs, bodyArgs) -> {

            // map tells where to get params from according to annotation
            final Map<Class<?>, Map<String, String>> annotationsMap = Map.of(
                    ArRoute.class, routeArgs == null ? Collections.emptyMap() : routeArgs,
                    ArQuery.class, queryArgs == null ? Collections.emptyMap() : queryArgs,
                    ArBody.class, bodyArgs == null ? Collections.emptyMap() : bodyArgs);


            Stream<Object> args = Arrays.stream(method.getParameters())     //Stream of args for method invoke
                    .map(p -> {
                        String pName = p.getName();
                        Class<?> pType = p.getType();

                        for (Class<?> a : annotationsMap.keySet()) {
                            if (!p.isAnnotationPresent((Class<? extends Annotation>) a)) continue;
                            try {
                                return Parser.parse(pName, pType, annotationsMap.get(a));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        throw new InvalidParameterException("Missing Annotation in Parameter: " + pName);
                    });
            try {
                return (Optional<?>) method.invoke(controller, args.toArray());  // Handler invokes the method and returns its results
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return new ArHttpRoute(method.getName(), annotation.method(), annotation.path(), handler);
    }
}