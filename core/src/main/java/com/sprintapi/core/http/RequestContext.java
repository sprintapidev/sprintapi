package com.sprintapi.core.http;

import io.undertow.server.HttpServerExchange;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    private final String method;  // GET, POST, etc.
    private final String path;    // /users/1
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final Object body;
    private final Map<String, Object> attributes = new HashMap<>(); // Custom data for middleware
    private final HttpServerExchange exchange;  // Raw request (if needed)

    public RequestContext(String method, String path,
                          Map<String, String> headers,
                          Map<String, String> queryParams,
                          Object body, HttpServerExchange exchange) {
        this.method = method;
        this.path = path;
        this.headers = Collections.unmodifiableMap(headers);
        this.queryParams = Collections.unmodifiableMap(queryParams);
        this.body = body;
        this.exchange = exchange;
    }

    // Getters
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public Map<String, String> getQueryParams() { return queryParams; }
    public Object getBody() { return body; }
    public HttpServerExchange getExchange() { return exchange; }

    // Middleware can attach custom attributes (e.g., decoded JWT user info)
    public void setAttribute(String key, Object value) { attributes.put(key, value); }
    public Object getAttribute(String key) { return attributes.get(key); }
}
