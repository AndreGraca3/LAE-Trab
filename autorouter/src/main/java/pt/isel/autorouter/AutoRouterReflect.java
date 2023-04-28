package pt.isel.autorouter;

import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;
import pt.isel.autorouter.utils.Parser;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

import static pt.isel.autorouter.MyParameter.getParameterInfo;


public class AutoRouterReflect extends AutoRouter {

    public AutoRouterReflect(Object controller) {
        super(controller);
    }

    // For each method check: annotated with @AutoRoute, returns Optional and create ArHttpRoute
    public Stream<ArHttpRoute> autorouterReflect() {
        return controllerMethods.map(m -> createArHttpRoute(controller, m.getAnnotation(AutoRoute.class), m));
    }

    private static ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method) {

        Parameter[] params = method.getParameters();

        // List with each parameter and its annotation
        List<MyParameter> myParams = Arrays.stream(params).map(p ->
                getParameterInfo(p, ArRoute.class, ArQuery.class, ArBody.class)
        ).toList();

        Map<Class<?>, Map<String, String>> annotationsMap = new HashMap<>(Map.of(
                ArRoute.class, new HashMap<>(),
                ArQuery.class, new HashMap<>(),
                ArBody.class, new HashMap<>()));

        ArHttpHandler handler = (routeArgs, queryArgs, bodyArgs) -> {

            annotationsMap.put(ArRoute.class, routeArgs);
            annotationsMap.put(ArQuery.class, queryArgs);
            annotationsMap.put(ArBody.class, bodyArgs);

            //for each param, get its annotation and use it to get param value from corresponding map
            Stream<Object> args = myParams.stream().map(param -> {
                String pName = param.name();
                Class<?> pType = param.type();
                try {
                    return Parser.parse(pName, pType, annotationsMap.get(param.annotation()));
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
}