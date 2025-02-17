package com.sprintapi.core.middleware.builtin;

import com.sprintapi.core.http.RequestContext;
import com.sprintapi.core.middleware.MiddlewareChain;
import com.sprintapi.core.middleware.MiddlewareHandler;
import com.sprintapi.processor.annotations.Middleware;

import java.util.logging.Logger;

@Middleware(order = 1)
public class SprintLoggingMiddleWare implements MiddlewareHandler {
    private static final Logger logger = Logger.getLogger(SprintLoggingMiddleWare.class.getName());

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean appliesTo(RequestContext ctx) {
        return isEnabled();
    }

    @Override
    public void handle(RequestContext ctx, MiddlewareChain next) {
        logger.info(String.format("Incoming Request: %s %s", ctx.getMethod(), ctx.getPath()));

        // Log Headers
        if (!ctx.getHeaders().isEmpty()) {
            logger.info("Headers: " + ctx.getHeaders());
        }

        // Log Query Params
        if (!ctx.getQueryParams().isEmpty()) {
            logger.info("Query Parameters: " + ctx.getQueryParams());
        }

        // Log Body if present
        if (ctx.getBody() != null) {
            logger.info("Request Body: " + ctx.getBody().toString());
        }

        next.next(ctx);
    }
}
