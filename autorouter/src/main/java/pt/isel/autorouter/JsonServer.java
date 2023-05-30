package pt.isel.autorouter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import org.jetbrains.annotations.NotNull;
import pt.isel.autorouter.annotations.ArSequenceResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static pt.isel.autorouter.WatchServiceKt.watchNewFilesContent;

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
                if (route.getClass().getDeclaredMethod(route.funName()).isAnnotationPresent(ArSequenceResponse.class)) {
                    handleSequenceResponse(ctx, res.get());
                }else {
                    ctx.json(res.get());
                }
            } else {
                // Status code 404
                throw new NotFoundResponse();
            }
        };
    }

    private static void handleSequenceResponse(Context ctx, Object sequence) throws IOException {
        ctx.contentType("text/html");
        PrintWriter writer = ctx.res().getWriter();
        Optional<?> optionalSequence = (Optional<?>) sequence;
        optionalSequence.ifPresentOrElse(
                seq -> {
                    for (Object item : (Iterable<?>) seq) {
                        if (item instanceof Iterable) {
                            for (Object subItem : (Iterable<?>) item) {
                                writer.println("<p>" + subItem.toString() + "</p>");
                                writer.flush();
                            }
                        } else {
                            writer.println("<p>" + item.toString() + "</p>");
                            writer.flush();
                        }
                    }
                },
                () -> {
                    // Status code 404
                    throw new NotFoundResponse();
                }
        );
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

    // Extension function to register a WatchService on a given Path and return a sequence of file contents
    public JsonServer JsonWatchNewFilesContent(Path path) {
        watchNewFilesContent(path);//.forEach(this::handleSequenceResponse);
        return this;
    }
}
