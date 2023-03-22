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



    private static ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method) {

        ArHttpHandler handler = (routeArgs, queryArgs, bodyArgs) -> {

            Stream<Object> args = Arrays.stream(method.getParameters())     //Stream of args for method invoke
                    .map(p -> {
                        String pName = p.getName();
                        if (p.isAnnotationPresent(ArRoute.class)) return routeArgs.get(pName);
                        if (p.isAnnotationPresent(ArQuery.class)) return queryArgs.get(pName);
                        if (p.isAnnotationPresent(ArBody.class)) return bodyArgs.get(pName);
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

    private <T> bodyParse(Class<?> type, Map<String,String> bodyArgs) throws NoSuchMethodException { // obj = Player(number=88,name="whatever")

        if(type.getConstructors().length != 1) throw new NoSuchMethodException();

        Parameter[] parameters = type.getConstructor().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            String pname = parameters[i].getName();
            Class<?> ptype = parameters[i].getType();
            String prop_val = bodyArgs.get(pname);
            if(prop_val != null){
                if(prop_val.)
            }
        }

    }
}