package pt.isel.autorouter;

import java.lang.reflect.Parameter;

public class MyParameter {
    private final Parameter param;
    private final Class<?> paramType;
    private final String paramName;
    private final Class<?> paramAnnotation;

    public MyParameter(Parameter param, Class<?> paramType, String paramName, Class<?> paramAnnotation) {
        this.param = param;
        this.paramType = paramType;
        this.paramName = paramName;
        this.paramAnnotation = paramAnnotation;
    }

    public Parameter getParam() {
        return param;
    }

    public Class<?> getParamType() {
        return paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public Class<?> getParamAnnotation() {
        return paramAnnotation;
    }
}
