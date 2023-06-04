package pt.isel.autorouter.routers;

import org.cojen.maker.ClassMaker;
import org.cojen.maker.MethodMaker;
import org.cojen.maker.Variable;
import pt.isel.autorouter.ArHttpHandler;
import pt.isel.autorouter.ArHttpRoute;
import pt.isel.autorouter.utils.MyParameter;
import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;


public class AutoRouterDynamic extends AutoRouter {

    public AutoRouterDynamic(Object controller) {
        super(controller);
    }

    private static final Map<Class<?>, Integer> mapIdx = new HashMap<>(Map.of(
            ArRoute.class, 0,
            ArQuery.class, 1,
            ArBody.class, 2));

    public Stream<ArHttpRoute> autorouterDynamic() {
        return controllerMethods
                .map(m -> {
                    AutoRoute annotation = m.getAnnotation(AutoRoute.class);
                    try {
                        return createArHttpRoute(controller, annotation, m);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private ArHttpRoute createArHttpRoute(Object controller, AutoRoute annotation, Method method) throws ReflectiveOperationException {
        Class<?> methodClass = buildHandler(controllerClass, method).finish();
        ArHttpHandler handler = (ArHttpHandler) methodClass.getDeclaredConstructor(controllerClass).newInstance(controller);
        return new ArHttpRoute(method.getName(), annotation.method(), annotation.path(), handler, annotation.returnType());
    }

    public static ClassMaker buildHandler(Class<?> controllerClass, Method method) {
        ClassMaker cm = ClassMaker.begin()
                .public_()
                .implement(ArHttpHandler.class);

        // Add the field that holds the controller instance
        cm.addField(controllerClass, "controller")
                .private_()
                .final_();

        // Creates the constructor and add a field to it.
        MethodMaker constructor = cm.addConstructor(controllerClass).public_();
        constructor.invokeSuperConstructor();
        constructor.field("controller").set(constructor.param(0));


        // Create the handle method
        MethodMaker handle = cm.addMethod(Optional.class, "handle", Map.class, Map.class, Map.class)
                .public_();

        Object[] args = Stream.of(method.getParameters())
                .map(parameter -> {
                    try {
                        return buildParamVariable(handle, parameter, null);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray();

        Variable res = handle.field("controller").invoke(method.getName(), args);

        handle.return_(res);

        return cm;
    }

    /**
     * Recursive Function to get a Variable from a param
     *
     * @param handle         - function where we get the maps with param value
     * @param parameter      - parameter to transform
     * @param prevAnnotation - the annotation to use if there is none in parameter
     * @return Variable from parameter
     * @throws NoSuchMethodException
     */
    private static Variable buildParamVariable(MethodMaker handle, Parameter parameter, Class<?> prevAnnotation) throws NoSuchMethodException {
        Class<?> annotation = MyParameter.findAnnotation(parameter, prevAnnotation, mapIdx.keySet().toArray(Class[]::new));

        int paramIdx = mapIdx.get(annotation);
        Class<?> parameterType = parameter.getType();

        // get value from corresponding map and cast to String
        Variable parameterValue = handle.param(paramIdx).invoke("get", parameter.getName()).cast(String.class);

        if (parameterType == String.class) {
            return handle.var(parameterType).set(parameterValue);
        }

        if (parameterType.isPrimitive()) {
            return buildPrimitiveParamVariable(handle, parameterType, parameterValue);
        }

        // From this point we know it is a complex Object, and we call the function recursively
        if (parameterType.getDeclaredConstructors().length != 1) throw new NoSuchMethodException();

        Constructor<?> c = parameterType.getDeclaredConstructors()[0];

        Variable[] cArgs = Arrays.stream(c.getParameters()).map(
                p -> {
                    try {
                        return buildParamVariable(handle, p, annotation);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(Variable[]::new);

        return handle.new_(parameterType, cArgs);
    }


    public static Variable buildPrimitiveParamVariable(MethodMaker handle, Class<?> parameterType, Variable parameterValue) {

        final Map<Class<?>, Class<?>> primitiveToWrapper = Map.of(
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                short.class, Short.class,
                byte.class, Byte.class,
                boolean.class, Boolean.class);

        Class<?> parserType = primitiveToWrapper.get(parameterType);
        return handle.var(parserType).invoke("parse" + firstToUpper(parameterType.getName()), parameterValue);
    }


    public static String firstToUpper(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}