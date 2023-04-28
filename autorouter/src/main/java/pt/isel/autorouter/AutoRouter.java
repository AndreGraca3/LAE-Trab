package pt.isel.autorouter;

import pt.isel.autorouter.annotations.AutoRoute;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;


public class AutoRouter {
    Object controller;
    Class<?> controllerClass;
    Stream<Method> controllerMethods;

    public AutoRouter(Object controller) {
        this.controller = controller;
        this.controllerClass = controller.getClass();
        this.controllerMethods = Arrays.stream(controllerClass.getDeclaredMethods()).filter(m ->
                m.isAnnotationPresent(AutoRoute.class) && m.getReturnType() == Optional.class
        );
    }
}