package pt.isel.autorouter;

import kotlin.jvm.internal.DefaultConstructorMarker;
import org.cojen.maker.ClassMaker;
import org.cojen.maker.FieldMaker;
import org.cojen.maker.MethodMaker;
import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class AutoRouterDynamic {

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
        FieldMaker controller = cm.addField(controllerClass, "controller")
                .private_()
                .final_();

        // Add the public constructor that takes the controller class as a parameter
//        cm.addMethod(void.class, "<init>", controllerClass)
//                .public_()
//                .param(0).set(controller);

        cm.addConstructor(controllerClass)
                .public_()
                .param(0).set(controller);


        // Create the handle method
        MethodMaker handle = cm.addMethod(Optional.class, "handle", Map.class, Map.class, Map.class)
                .public_();

        // Get the parameters of the method
        Class<?>[] parameters = method.getParameterTypes();
//        for (Class<?> parameter : parameters) {
//            if(parameter.isPrimitive()){
//                System.out.println("Primitive");
//            } else {
//                System.out.println("Not primitive");
//            }
//        }







        // Add the code to call the controller method
//        handle.var(0).invoke(method.getName(),);


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
