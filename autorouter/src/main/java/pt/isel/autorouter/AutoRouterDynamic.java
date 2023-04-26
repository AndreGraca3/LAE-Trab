package pt.isel.autorouter;

import org.cojen.maker.ClassMaker;
import org.cojen.maker.FieldMaker;
import org.cojen.maker.MethodMaker;
import org.cojen.maker.Variable;
import pt.isel.autorouter.annotations.ArBody;
import pt.isel.autorouter.annotations.ArQuery;
import pt.isel.autorouter.annotations.ArRoute;
import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;


public class AutoRouterDynamic {


    private static final Map<Class<?>, Integer> mapIndex = new HashMap<>(Map.of(
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
        return Stream.of(controller.getClass().getDeclaredMethods())
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

        Object[] args = Stream.of(method.getParameters())
                .map(parameter -> {
                    try {
                        return buildParameterVariable(handle, parameter);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray();

        Variable res = handle.field("controller").invoke(method.getName(),args);

        handle.return_(res);

        return cm;
    }

    private static Variable buildParameterVariable(MethodMaker handle, Parameter parameter) throws NoSuchMethodException {
        Class<?>[] annotations = new Class[] {ArRoute.class, ArQuery.class, ArBody.class };
        Class<?> annotation = MyParameter.findAnnotation(parameter, annotations);

        int paramIdx = mapIndex.get(annotation);
        Class<?> parameterType = parameter.getType();

        // get map value in String
        Variable parameterValue = handle.param(paramIdx).invoke("get", parameter.getName()).cast(String.class); // "i42d"

        if(parameterType == String.class) {
            return handle.var(parameterType).set(parameterValue);
        }

        if(parameterType.isPrimitive()) {
            Class<?> parserType = primitiveToWrapper.get(parameterType);
            return handle.var(parserType).invoke("parse" + firstToUpper(parameterType.getName()),parameterValue);
        }

        // if type == complex
        if(parameterType.getDeclaredConstructors().length != 1) throw new NoSuchMethodException();

        Class<?>[] constructorParamTypes = Arrays.stream(parameterType.getDeclaredFields()).map(Field::getType).toArray(Class[]::new);

        Stream<Variable> constructorParam = Arrays.stream(parameterType.getDeclaredConstructor(constructorParamTypes).getParameters()).map(it -> {
            try {
                return buildParameterVariable(handle,it); // parameterType
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        return handle.var(parameterType).invoke(parameterType.getDeclaredConstructor(constructorParamTypes).getName(),constructorParam);
    }


    public static String firstToUpper(String parameterType) {
        return parameterType.substring(0,1).toUpperCase() + parameterType.substring(1);
        //return parameterType.replaceFirst("/^./", String.valueOf(parameterType.indexOf(0)));
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


//    public class AddStudentClassroomControllerClass implements ArHttpHandler {
//
//        private final ClassroomController controller;
//
//        public AddStudentClassroomControllerClass(ClassroomController controller) {
//            this.controller = controller;
//        }
//
//        @Override
//        public Optional<?> handle(Map<String, String> routeArgs, Map<String, String> queryArgs, Map<String, String> bodyArgs) {
//            controller.addStudent(routeArgs.get("classroom"), routeArgs.get("nr"), bodyArgs.get("student"));
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


 /*Variable map1 = handle.param(0);
        Variable classroomVar = map1.invoke("get", "classroom").cast(String.class);

        Variable map2 = handle.param(1);
        Variable studentVar = map2.invoke("get", "student").cast(String.class);

        Variable classroom = handle.var(String.class).set(classroomVar);
        Variable student = handle.var(String.class).set(studentVar);*/



// Get the parameters of the method
//Class<?>[] parameters = method.getParameterTypes();