package com.sprintapi.core.routing;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;


public class RouteDispatcher implements HttpHandler {


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String requestMethod = String.valueOf(exchange.getRequestMethod());
        String requestUri = exchange.getRequestPath();

        Route matchedRoute = findRoute(requestMethod, requestUri);
        if (matchedRoute == null){
            exchange.getResponseSender().send("404");
            return;
        }
        matchedRoute.invoke(exchange);
    }

    public Route findRoute(String requestMethod, String requestUri) {
        return RouteRegistry.routes.stream()
                .filter(route -> RouteUtilsProvider.matchRouteWithRequest(route.getAnnotationPath(), route.getAnnotationMethodType(), requestUri, requestMethod))
                .findFirst()
                .orElse(null);
    }
}
