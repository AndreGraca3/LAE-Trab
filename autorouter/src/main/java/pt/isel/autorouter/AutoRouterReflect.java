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
        // Only to get methods or all declaredMembers?
        // TODO: Should the controller inputs be public or not?

        var controllerClass = controller.getClass();

        for (Method m : controllerClass.getDeclaredMethods()){
            System.out.println("Method: " + m.getName());
            System.out.println("Parameters: ");
            for(Parameter p : m.getParameters()) System.out.println("\t" + p);
            for(Annotation a : m.getDeclaredAnnotations()) System.out.println("\t" + a);
            System.out.println("Return Type: " + m.getReturnType());
            System.out.println("---+---+---+---+---+---");
        }
        return Stream.empty();
    }
}
