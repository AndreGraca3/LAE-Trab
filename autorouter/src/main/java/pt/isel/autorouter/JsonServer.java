package pt.isel.autorouter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class JsonServer implements AutoCloseable {

    final Javalin server = Javalin.create();
    final static ObjectMapper mapper = new ObjectMapper();

    public JsonServer(Stream<ArHttpRoute> routes) {
        routes.forEach(this::addRoute);
    }

    /**
     * Parses body request as Json and return Json back.
     */
    public final JsonServer addRoute(ArHttpRoute route) {
        Handler handler = httpHandlerForRoute(route);
        switch (route.method()) {
            case GET -> server.get(route.path(), handler);
            case POST -> server.post(route.path(), handler);
            case DELETE -> server.delete(route.path(), handler);
            case PUT -> server.put(route.path(), handler);
        };
        return this;
    }

    /**
     * Creates a Javalin Handler for an autorouter ArHttpRoute.
     * Parses body request as Json.
     */
    private static Handler httpHandlerForRoute(ArHttpRoute route) {
        return ctx -> {
            var routeArgs = ctx.pathParamMap();
            var queryArgs = ctx.queryParamMap().entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().get(0)));
            var bodyArgs = ctx.body().isEmpty() ? null : mapper.readValue(ctx.body(), Map.class);
            var res = route.handler().handle(routeArgs, queryArgs, bodyArgs);
            if (res.isPresent()) {
                System.out.println("Cheguei aqui1!");
                switch (route.returnType()) {
                    case SEQUENCE -> handleSequenceResponse(ctx, res.get());
                    case OBJECT -> ctx.json(res.get());
                }
            } else {
                // Status code 404
                throw new NotFoundResponse();
            }
        };
    }

    private static void handleSequenceResponse(Context ctx, Object sequence) throws Exception {
        ctx.contentType("text/html");
        //PrintWriter writer = ctx.res().getWriter();
        Optional<?> optionalSequence =  Optional.of(sequence);
        System.out.println("Cheguei aqui2!");
        handleSequence(ctx, optionalSequence);
        optionalSequence.ifPresentOrElse(
                seq -> handleSequence(ctx, seq),
                () -> {
                    // Status code 404
                    throw new NotFoundResponse();
                }
        );
    }

    @SuppressWarnings("unchecked")
    private static void handleSequence(Context ctx, Object sequence) {
        if (sequence instanceof Iterable) {
            System.out.println("Cheguei aqui3!");
            for (Object item : (Iterable<?>) sequence) {
                handleSequence(ctx, item);
            }
        } else {
            System.out.println("Cheguei aqui4!");
            ctx.result("<p>" + sequence.toString() + "</p>");
        }
    }

    public void start(int port) {
        server.start(port);
    }

    @Override
    public void close() {
        server.close();
    }

    @NotNull
    public Javalin javalin() {
        return server;
    }
}
