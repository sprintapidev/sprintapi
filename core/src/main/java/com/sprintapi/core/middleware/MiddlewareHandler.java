package com.sprintapi.core.middleware;

import com.sprintapi.core.http.RequestContext;

public interface MiddlewareHandler {
    default boolean isEnabled() {
        return true; // By default, all middleware is enabled
    }
    boolean appliesTo(RequestContext ctx); // Decides if it should run
    void handle(RequestContext ctx, MiddlewareChain next);
}
