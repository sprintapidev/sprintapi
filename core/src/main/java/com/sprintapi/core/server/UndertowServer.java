package com.sprintapi.core.server;

import com.sprintapi.core.routing.RouteDispatcher;
import io.undertow.Undertow;

public class UndertowServer {

    public static void start(int portNumber) {
        Undertow server = Undertow.builder()
                .addHttpListener(portNumber, "0.0.0.0")
                .setHandler(new RouteDispatcher())
                .build();

        server.start();
        System.out.println("Sprint API server running on http://localhost:" + portNumber);
    }
}