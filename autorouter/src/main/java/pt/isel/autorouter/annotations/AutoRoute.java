package pt.isel.autorouter.annotations;

import pt.isel.autorouter.ArVerb;
import pt.isel.autorouter.returnType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static pt.isel.autorouter.returnType.OBJECT;
import static pt.isel.autorouter.ArVerb.GET;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRoute {
    String path();
    ArVerb method() default GET;
    returnType returnType() default OBJECT;
}
