package pt.isel.autorouter;

import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Stream;


public class AutoRouterReflect {

    public static Stream<ArHttpRoute> autorouterReflect(Object controller) {

        // class of controller object
        Class<?> controllerClass = controller.getClass();

        // List of routes
        List<ArHttpRoute> routes = new ArrayList<>();

        // Looping the declared methods of the class
        for (Method method : controllerClass.getDeclaredMethods()) {

            // Check if the method is annotated with @AutoRoute and ReturnType Optional
            if (method.isAnnotationPresent(AutoRoute.class) &&
                    method.getReturnType().isAssignableFrom(Optional.class)) {

                // Get annotation AutoRoute.
                AutoRoute routeAnnotation = method.getAnnotation(AutoRoute.class);

                // Method to create arHttpRoute
                ArHttpRoute route = createArHttpRoute(controller, routeAnnotation, method);

                // Add the ArHttpRoute object to the list
                routes.add(route);
            }
        }

        // Return the stream of ArHttpRoute objects
        return routes.stream();
    }


    private static ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method) {

        // Creation of ArHttpRoute
        return new ArHttpRoute(method.getName(), annotation.method(), annotation.path(),
                (routeArgs, queryArgs, bodyArgs) -> {

                    // Get method parameters
                    Parameter[] parameters = method.getParameters();

                    // Array of args to prepare method invoke
                    Object[] args = new Object[parameters.length];

                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        String parameterName = parameter.getName();

                            //@ArRoute Annotation and get arg from map
                        if (parameter.isAnnotationPresent(ArRoute.class)) {
                            args[i] = routeArgs.get(parameterName);

                            //@ArQuery Annotation and get arg from map
                        } else if (parameter.isAnnotationPresent(ArQuery.class)) {
                            args[i] = queryArgs.get(parameterName);

                            //@ArBody Annotation and get arg from map
                        } else if (parameter.isAnnotationPresent(ArBody.class)) {
                            args[i] = bodyArgs.get(parameterName);

                            // Invalid Parameter Annotation
                        } else
                            throw new InvalidParameterException("Missing Annotation in Parameter: " + parameterName);
                    }
                    try {
                        // return the handler of this ArHttpRoute object
                        return (Optional<?>) method.invoke(controller, args);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}
