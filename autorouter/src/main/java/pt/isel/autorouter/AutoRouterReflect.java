package pt.isel.autorouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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
            if (method.isAnnotationPresent(AutoRouter.class) &&
                    Either.class.equals(method.getReturnType())) { // check it out TODO()

                // Get the method and path from the annotation
                AutoRouter routeAnnotation = method.getAnnotation(AutoRouter.class);
                ArVerb verbMethod = routeAnnotation.method();
                String path = routeAnnotation.path();

                // Create the ArHttpRoute object
//                ArHttpRoute route = new ArHttpRoute(httpMethod, path, ctx -> {
//                    TODO()
//                });

                // Add the ArHttpRoute object to the list
                routes.add(route);
            }
        }

        // Return the stream of ArHttpRoute objects
        return routes.stream();
    }

}
