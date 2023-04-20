package pt.isel.autorouter;

import kotlin.jvm.internal.DefaultConstructorMarker;
import org.cojen.maker.ClassMaker;
import org.cojen.maker.FieldMaker;
import org.cojen.maker.MethodMaker;
import org.cojen.maker.Variable;
import org.eclipse.jetty.util.StringUtil;
import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Character.*;

public class AutoRouterDynamic {


    private static Map<Class<?>, Integer> mapIndex = new HashMap<>(Map.of(
            ArRoute.class, 0,
            ArQuery.class, 1,
            ArBody.class, 2));

    private static final Map<Class<?>, Class<?>> primitiveToWrapper = Map.of(
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            short.class, Short.class,
            byte.class, Byte.class,
            boolean.class, Boolean.class);
    public static Stream<ArHttpRoute> autorouterDynamic(Object controller) {
        return Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AutoRoute.class))
                .map(method -> {
                    AutoRoute autoRoute = method.getAnnotation(AutoRoute.class);
                    String path = autoRoute.path();
                    ArVerb verb = autoRoute.method();
                    try {
                        Class<?> methodClass = buildHandler(controller.getClass(), method).finish();
                        ArHttpHandler handler = (ArHttpHandler) methodClass.getDeclaredConstructor(controller.getClass()).newInstance(controller);
                        return new ArHttpRoute(method.getName(), verb, path, handler);
                    } catch (ReflectiveOperationException ex) {
                        System.err.println("Error creating handler for method " + method.getName() + ": " + ex.getMessage());
                    }
                    return null;
                });
    }

    public static ClassMaker buildHandler(Class<?> controllerClass, Method method) {
        ClassMaker cm = ClassMaker.begin()
                .public_()
                .implement(ArHttpHandler.class);

        // Add the field that holds the controller instance
        FieldMaker controller = cm.addField(controllerClass, "controller") // object / name
                .private_()
                .final_();

        // Creates the constructor and add a field to it.
        MethodMaker constructor = cm.addConstructor(controllerClass).public_();
        constructor.invokeSuperConstructor();
        constructor.field("controller").set(constructor.param(0));


        // Create the handle method
        MethodMaker handle = cm.addMethod(Optional.class, "handle", Map.class, Map.class, Map.class)
               .public_();

        //Object[] args = Stream.of(method.getParameters()).map(parameter -> buildParameterVariable(handle, parameter)).toArray();

        Variable map1 = handle.param(0);
        Variable classroomVar = map1.invoke("get", "classroom").cast(String.class);

        Variable map2 = handle.param(1);
        Variable studentVar = map2.invoke("get", "student").cast(String.class);

        Variable classroom = handle.var(String.class).set(classroomVar);
        Variable student = handle.var(String.class).set(studentVar);

        Variable res = handle.field("controller").invoke(method.getName(), classroom, student);

        handle.return_(res);

        // Get the parameters of the method
        //Class<?>[] parameters = method.getParameterTypes();

        return cm;
    }



//    public class SearchClassroomControllerClass implements ArHttpHandler {
//
//        private final ClassroomController controller;
//
//        public SearchClassroomControllerClass(ClassroomController controller) {
//            this.controller = controller;
//        }
//
//        @Override
//        public Optional<?> handle(Map<String, String> routeArgs, Map<String, String> queryArgs, Map<String, String> bodyArgs) {
//            controller.search(routeArgs.get("classroom"), queryArgs.get("student"));
//        }
//    }
}


// Add the public constructor that takes the controller class as a parameter
//        cm.addMethod(void.class, "<init>", controllerClass)
//                .public_()
//                .param(0).set(controller);

//        cm.addConstructor(controllerClass)
//                .public_()
//                .param(0).set(controller);




// Add the code to call the controller method
//        handle.var(0).invoke(method.getName(),);

