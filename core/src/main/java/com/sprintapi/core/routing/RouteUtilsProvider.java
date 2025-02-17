package com.sprintapi.core.routing;

import com.dslplatform.json.DslJson;
import com.sprintapi.core.http.RequestContext;
import com.sprintapi.core.middleware.MiddlewareChain;
import com.sprintapi.core.http.SprintHttpResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteUtilsProvider {
    private static final DslJson<Object> dslJson = new DslJson<>();

    public static <T> void withRequestBody(HttpServerExchange exchange, Class<T> clazz, Consumer<T> callback) {
        if (exchange.getRequestContentLength() > 0) {
            exchange.getRequestReceiver().receiveFullBytes((ex, data) -> {
                String receivedBody = new String(data, StandardCharsets.UTF_8);
                System.out.println("Received body: " + receivedBody);
                if (data.length > 0) {
                    try {
                        T obj = dslJson.deserialize(clazz, data, data.length);
                        MiddlewareChain requestChain = new MiddlewareChain(ctx -> {
                            try {
                                callback.accept(obj);  // Pass deserialized object
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                        requestChain.next(getRequestContext(exchange, obj));
                    } catch (Exception e) {
                        callback.accept(null); // Instead of throwing, pass null
                    }
                } else {
                    callback.accept(null); // No body found, pass null
                }
            });
        } else {
            callback.accept(null); // No content length, assume no body
        }
    }

    public static <T> void withoutBody(HttpServerExchange exchange, Runnable runnable) {
        MiddlewareChain requestChain = new MiddlewareChain(ctx -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        requestChain.next(getRequestContext(exchange, null));
    }

    public static <T> T getPathParam(HttpServerExchange exchange, String pathTemplate, String key, Class<T> targetType) throws IOException {
        String requestUri = getRequestUri(exchange);
        List<String> paramNames = new ArrayList<>();
        Matcher pathMatcher = Pattern.compile("\\{([^}]+)}").matcher(pathTemplate);
        while (pathMatcher.find()) {
            paramNames.add(pathMatcher.group(1));
        }


        Map<String, String> params = new HashMap<>();
        String regex = pathTemplate.replaceAll("\\{([^}]+)}", "([^/]+)");
        Matcher requestMatcher = Pattern.compile(regex).matcher(requestUri);

        if (requestMatcher.matches()) {
            for (int i = 0; i < paramNames.size(); i++) {
                params.put(paramNames.get(i), requestMatcher.group(i + 1));
            }
        }

        String value = params.get(key);
        if (targetType == String.class) {
            return targetType.cast(value);
        }
        return dslJson.deserialize(targetType, new ByteArrayInputStream(value.getBytes()));
    }

    public static <T> T getQueryParam(HttpServerExchange exchange, String key, Class<T> targetType) throws IOException {
        Deque<String> stringDeque = exchange.getQueryParameters().get(key);
        if(stringDeque!=null){
            String value = stringDeque.getFirst();
            if (targetType == String.class) {
                return targetType.cast(value);
            }
            return dslJson.deserialize(targetType, new ByteArrayInputStream(value.getBytes()));
        }
        return null;
    }

    public static boolean matchRouteWithRequest(String pathTemplate, String pathMethodType, String requestUri, String requestMethod) {
        String regex = pathTemplate.replaceAll("\\{[^}]+}", "([^/]+)");
        return Pattern.matches(regex, requestUri) && pathMethodType.equals(requestMethod);
    }

    public static String getRequestUri(HttpServerExchange exchange){
        return exchange.getRequestPath();
    }

    public static String getRequestMethod(HttpServerExchange exchange){
        return String.valueOf(exchange.getRequestMethod());
    }

    public static <T> T parseParamToTargetType(String value, Class<T> targetType) {
        try {
            if (targetType == String.class) {
                return targetType.cast(value);
            }
            return dslJson.deserialize(targetType, new ByteArrayInputStream(value.getBytes()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse value: " + value + " to " + targetType.getSimpleName());
        }
    }

    public static void processResponse(HttpServerExchange exchange, Object response, Class<?> responseType) throws IOException {
        if (responseType == SprintHttpResponse.class){
            SprintHttpResponse r = (SprintHttpResponse) responseType.cast(response);
            exchange.setStatusCode(r.getStatusCode());
            r.getHeaders().forEach((String key, String value) -> exchange.getResponseHeaders().put(HttpString.tryFromString(key), value));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dslJson.serialize(r.getBody(), outputStream);

            // Convert JSON to String
            String jsonBody = outputStream.toString(StandardCharsets.UTF_8);
            exchange.getResponseSender().send(jsonBody);
        } else{
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            dslJson.serialize(response, outputStream);

            // Convert JSON to String
            String jsonBody = outputStream.toString(StandardCharsets.UTF_8);
            exchange.getResponseSender().send(jsonBody);
        }
    }

    public static void processVoidResponse(HttpServerExchange exchange){
        exchange.endExchange();
    }

    public static Map<String, String> getHeaders(HttpServerExchange exchange) {
        Map<String, String> headersMap = new HashMap<>();
        HeaderMap headers = exchange.getRequestHeaders();

        for (HttpString headerName : headers.getHeaderNames()) {
            headersMap.put(headerName.toString(), headers.getFirst(headerName));
        }
        return headersMap;
    }

    public static Map<String, String> getQueryParams(HttpServerExchange exchange) {
        Map<String, String> queryParamsMap = new HashMap<>();

        exchange.getQueryParameters().forEach((key, values) -> {
            queryParamsMap.put(key, values.getFirst());
        });

        return queryParamsMap;
    }

    public static RequestContext getRequestContext(HttpServerExchange exchange, Object body){
        return new RequestContext(
                getRequestMethod(exchange),
                getRequestUri(exchange),
                getHeaders(exchange),
                getQueryParams(exchange),
                body,
                exchange);
    }

}
