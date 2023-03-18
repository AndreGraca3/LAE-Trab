package pt.isel.autorouter;

import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

        ArVerb verbMethod = annotation.method(); // returns http method.
        String path = annotation.path();

        Map<String, String> RouteArgs = new HashMap<>();
        Map<String, String> QueryArgs = new HashMap<>();
        Map<String, String> BodyArgs = new HashMap<>();

        for(int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            //System.out.println(parameter.getName());
            if(parameter.isAnnotationPresent(ArRoute.class)){
                String name = parameter.getName();
                String value = parameter.toString();
                RouteArgs.put(name,value);
            } else continue;
            if(parameter.isAnnotationPresent(ArQuery.class)){
                String name = parameter.getName();
                String value = parameter.toString();
                QueryArgs.put(name,value);
            } else continue;
            if(parameter.isAnnotationPresent(ArBody.class)){
                String name = parameter.getName();
                String value = parameter.toString();
                BodyArgs.put(name,value);
            }
        }

        //ArHttpHandler handler;
        //handler.handle(RouteArgs,QueryArgs,BodyArgs);

        return new ArHttpRoute(method.getName(),verbMethod,path, null);
    }
}
