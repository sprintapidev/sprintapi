package com.sprintapi.core;

import com.sprintapi.core.server.UndertowServer;
import com.sprintapi.processor.annotations.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SprintApp {
    private static final int PORT = 8089;

    public static void run() throws Exception {
        run(PORT);
    }

    public static void run(int portNumber) throws Exception {
        Logger.getLogger("org.xnio").setLevel(Level.WARNING);
        Logger.getLogger("org.jboss.threads").setLevel(Level.WARNING);
        Logger.getLogger("io.undertow").setLevel(Level.WARNING);
        String bannerTxt = readBannerFromResources();
        System.out.println(bannerTxt);
        UndertowServer.start(portNumber);
    }

    private static String readBannerFromResources() {
        try (InputStream input = SprintApp.class.getClassLoader().getResourceAsStream("banner.txt")) {
            if (input == null) {
                return "Welcome to Sprint API...";
            }
            Scanner scanner = new Scanner(input, StandardCharsets.UTF_8);
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            return "Error reading file!";
        }
    }
}
