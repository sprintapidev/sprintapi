package com.sprintapi.processor.generators;

import com.sprintapi.processor.dto.MiddlewareHandlerInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;

public class MiddleWareRegistryGenerator {
    public static void generateMiddlewareRegistry(ProcessingEnvironment processingEnv, List<MiddlewareHandlerInfo> middlewares){
        String packageName = "com.sprintapi.core.middleware";

        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + ".MiddlewareRegistry");
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.println("package " + packageName + ";");
                out.println("import java.util.HashMap;");
                out.println("import java.util.Map;");
                out.println("import java.util.List;");
                out.println("import java.util.ArrayList;");


                out.println("public class MiddlewareRegistry {");
                out.println("    private static final List<com.sprintapi.core.middleware.MiddlewareHandler> registeredMiddlewares = new ArrayList<>();");
                out.println("    static {");

                middlewares.sort(Comparator.comparingInt(MiddlewareHandlerInfo::getOrder));
                for (MiddlewareHandlerInfo middlewareHandlerInfo : middlewares) {
                    out.println("        register(com.sprintapi.core.di.DIContainer.getInstance(" + middlewareHandlerInfo.getClassName() + ".class));");
                }
                out.println("    }");
                out.println("    public static void register(com.sprintapi.core.middleware.MiddlewareHandler middleware) {");
                out.println("        registeredMiddlewares.add(middleware);");
                out.println("    }");
                out.println("");
                out.println("    public static List<MiddlewareHandler> getMiddlewares() {");
                out.println("        return registeredMiddlewares;");
                out.println("    }");
                out.println("");
                out.println("}");
            }
        } catch (IOException e) {
            System.out.println("Exception occurred while generating Route registry class: " + e.getMessage());
        }
    }
}
