package pt.isel.autorouter;

import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class AutoRouterReflect {
    public static Stream<ArHttpRoute> autorouterReflect(Object controller) {

        // class of controller object
        Class<?> controllerClass = controller.getClass();

        // List of routes
        List<ArHttpRoute> routes = new ArrayList<>();

        // Looping the declared methods of the class
        for (Method method : controllerClass.getDeclaredMethods()) {

            // Check if the method is annotated with @AutoRoute and returns an Either ..check this out later TODO()
            if (method.isAnnotationPresent(AutoRoute.class) &&
                    method.getReturnType().isAssignableFrom(Optional.class)) { // check it out TODO()

                // Get annotation AutoRoute.
                AutoRoute routeAnnotation = method.getAnnotation(AutoRoute.class);

                ArHttpRoute route = createArHttpRoute(controller,routeAnnotation,method);

                // Add the ArHttpRoute object to the list
                routes.add(route);
            }
        }
        // Printing for Test
        for(ArHttpRoute route : routes){
            System.out.println(route);
        }

        // Return the stream of ArHttpRoute objects
        return routes.stream();
    }


    private static ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method) {

        // Get the method and path from the annotation
        ArVerb verbMethod = annotation.method();
        String path = annotation.path();

        return null;
    }
}
