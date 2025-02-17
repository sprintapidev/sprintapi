package com.sprintapi.processor.generators;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class RouteRegistryGenerator {
    public static void generateRouteRegistry(ProcessingEnvironment processingEnv, List<String> routeEntries) {
        String packageName = "com.sprintapi.core.routing";

        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + ".RouteRegistry");
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.println("package " + packageName + ";");
                out.println("import java.util.HashMap;");
                out.println("import java.util.Map;");
                out.println("import java.util.List;");
                out.println("import java.util.ArrayList;");


                out.println("public class RouteRegistry {");
                out.println("    public static final List<Route> routes = new ArrayList<>();");
                out.println("    static {");

                for (String routeClassName : routeEntries) {
                    out.println("        routes.add(new " + routeClassName + "());");
                }
                out.println("    }");
                out.println("}");
            }
        } catch (IOException e) {
            System.out.println("Exception occured while generating Route registry class: " + e.getMessage());
        }
    }
}
