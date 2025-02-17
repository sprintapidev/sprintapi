package com.sprintapi.core.middleware;

import com.sprintapi.core.http.RequestContext;

import java.util.Iterator;
import java.util.function.Consumer;

public class MiddlewareChain {
    private final Consumer<RequestContext> finalHandler;
    private Iterator<MiddlewareHandler> iterator;

    public MiddlewareChain(Consumer<RequestContext> finalHandler) {
        this.iterator = MiddlewareRegistry.getMiddlewares().iterator();
        this.finalHandler = finalHandler;
    }

    public void next(RequestContext ctx) {
        if (iterator.hasNext()) {
            MiddlewareHandler middleware = iterator.next();
            if (middleware.appliesTo(ctx)) {
                middleware.handle(ctx, this);
            } else {
                next(ctx); // Skip middleware if not applicable
            }
        } else {
            finalHandler.accept(ctx); // Execute final logic
        }
    }
}
