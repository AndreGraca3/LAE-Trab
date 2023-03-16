package pt.isel.autorouter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record ArResponse(int statusCode, Map<String, String> headers, Object body) {

    public static ArResponse empty() {
        return new ArResponse(204, Collections.emptyMap(), null);
    }

    public static ArResponse json(Object body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new ArResponse(200, headers, body);
    }

    public static ArResponse status(int statusCode, String message) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new ArResponse(statusCode, headers, message);
    }
}
