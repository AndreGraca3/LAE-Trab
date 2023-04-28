package pt.isel.autorouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.security.InvalidParameterException;
import java.util.Arrays;

public record MyParameter(Parameter param, Class<?> type, String name, Class<?> annotation) {

    static MyParameter getParameterInfo(Parameter p, Class<?>... annotations) {
        return new MyParameter(p, p.getType(), p.getName(), findAnnotation(p, null, annotations));
    }

    /**
     * Finds the first annotation in the given array that is present in the specified parameter.
     *
     * @param p              - The parameter to check for the annotation.
     * @param prevAnnotation - The previous annotation. If no annotation is found in the parameter and this is not null,
     *                       the previous annotation will be returned instead.
     * @param annotations    - The annotations to check for.
     * @return The first annotation found in the parameter or the previous annotation if no annotation is found and a
     * previous annotation is provided.
     */
    static Class<?> findAnnotation(Parameter p, Class<?> prevAnnotation, Class<?>... annotations) {
        try {
            return Arrays.stream(annotations).filter(
                    a -> p.isAnnotationPresent((Class<? extends Annotation>) a)
            ).findFirst().orElseThrow(() -> new InvalidParameterException("Missing Annotation in Parameter: " + p.getName()));
        } catch (InvalidParameterException e) {
            if (prevAnnotation == null) throw e;
            return prevAnnotation;
        }
    }
}