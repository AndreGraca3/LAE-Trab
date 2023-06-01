package pt.isel.autorouter;

public record ArHttpRoute(String funName, ArVerb method, String path, ArHttpHandler handler, ReturnType returnType) {
    public ArHttpRoute(String funName, ArVerb method, String path, ArHttpHandler handler) {
        this(funName, method, path, handler, ReturnType.OBJECT);
    }
}

