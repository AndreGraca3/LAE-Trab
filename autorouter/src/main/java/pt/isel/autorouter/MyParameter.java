package pt.isel.autorouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.Arrays;

public record MyParameter(Parameter param, Class<?> paramType, String paramName, Class<?> paramAnnotation) {

    static MyParameter getParameterInfo(Parameter p, Class<?>... annotations) {
        return new MyParameter(p, p.getType(), p.getName(), findAnnotation(p, annotations));
    }

    private static Class<?> findAnnotation(Parameter p, Class<?>... annotations) {
        return Arrays.stream(annotations).filter(
                a -> p.isAnnotationPresent((Class<? extends Annotation>) a)
        ).findFirst().orElseThrow(() -> new InvalidParameterException("Missing Annotation in Parameter: " + p.getName()));
    }
}
