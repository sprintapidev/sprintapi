package com.sprintapi.processor.generators;

import com.sprintapi.processor.dto.MethodParamInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class RouteGenerator {
    public static String generateRouteClass(
            ProcessingEnvironment processingEnv,
            String controllerFullName,
            String controllerClassName,
            String methodName,
            String annotationPath,
            String annotationMethodType,
            List<MethodParamInfo> paramInfoList,
            String bodyParamClassName,
            Boolean isBodyExist,
            String returnType
    ) {
        String packageName = "com.sprintapi.core.routing";
        String className = controllerClassName + "_" + methodName + "_Route";

        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + className);
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                // Package declaration
                out.println("package " + packageName + ";");
                out.println();
                out.println("import java.util.*;");
                out.println("import java.io.IOException;");
                out.println("import io.undertow.server.HttpServerExchange;");
                out.println();

                // Class definition
                out.println("public class " + className + " extends Route {");
                out.println();

                // Constructor
                out.println("    public " + className + "() {");
                out.println("        setAnnotationPath(\"" + annotationPath + "\");");
                out.println("        setAnnotationMethodType(\"" + annotationMethodType + "\");");
                out.println("    }");
                out.println();

                // Method: invoke()
                out.println("    public void invoke(HttpServerExchange exchange) throws Exception {");

                if (isBodyExist) {
                    if(!returnType.equals("void")){
                        out.println("        RouteUtilsProvider.withRequestBody(exchange, " + bodyParamClassName + ".class, (requestBody) -> {");
                        out.println("            try {");
                        out.println("                " + returnType + " res = com.sprintapi.core.di.DIContainer.getInstance(" + controllerFullName + ".class)");
                        out.println("                    ." + methodName + "(");

                        for (int i = 0; i < paramInfoList.size(); i++) {
                            MethodParamInfo param = paramInfoList.get(i);
                            out.print("                        ");
                            switch (param.type) {
                                case "QUERY" ->
                                        out.print("RouteUtilsProvider.getQueryParam(exchange, \"" + param.name + "\", " + param.classType + ".class)");
                                case "PATH" ->
                                        out.print("RouteUtilsProvider.getPathParam(exchange, getAnnotationPath(), \"" + param.name + "\", " + param.classType + ".class)");
                                case "BODY" -> out.print("requestBody");
                            }
                            if (i < paramInfoList.size() - 1) {
                                out.print(",");
                            }
                            out.println();
                        }

                        out.println("                    );");
                        out.println("                System.out.println(res);");
                        out.println("                RouteUtilsProvider.processResponse(exchange, res, " + returnType + ".class);");
                        out.println("            } catch (IOException e) {");
                        out.println("                throw new RuntimeException(e);");
                        out.println("            }");
                        out.println("        });");
                    }
                    else{
                        out.println("        RouteUtilsProvider.withRequestBody(exchange, " + bodyParamClassName + ".class, (requestBody) -> {");
                        out.println("            try {");
                        out.println("                 com.sprintapi.core.di.DIContainer.getInstance(" + controllerFullName + ".class)");
                        out.println("                    ." + methodName + "(");

                        for (int i = 0; i < paramInfoList.size(); i++) {
                            MethodParamInfo param = paramInfoList.get(i);
                            out.print("                        ");
                            switch (param.type) {
                                case "QUERY" ->
                                        out.print("RouteUtilsProvider.getQueryParam(exchange, \"" + param.name + "\", " + param.classType + ".class)");
                                case "PATH" ->
                                        out.print("RouteUtilsProvider.getPathParam(exchange, getAnnotationPath(), \"" + param.name + "\", " + param.classType + ".class)");
                                case "BODY" -> out.print("requestBody");
                            }
                            if (i < paramInfoList.size() - 1) {
                                out.print(",");
                            }
                            out.println();
                        }

                        out.println("                    );");
                        out.println("                RouteUtilsProvider.processVoidResponse(exchange);");
                        out.println("            } catch (IOException e) {");
                        out.println("                throw new RuntimeException(e);");
                        out.println("            }");
                        out.println("        });");
                    }

                } else {
                    if (!returnType.equals("void")){
                        out.println("        RouteUtilsProvider.withoutBody(exchange, () -> {");
                        out.println("            try {");

                        out.println("        " + returnType + " res =  com.sprintapi.core.di.DIContainer.getInstance(" + controllerFullName + ".class)");
                        out.println("            ." + methodName + "(");

                        for (int i = 0; i < paramInfoList.size(); i++) {
                            MethodParamInfo param = paramInfoList.get(i);
                            out.print("                ");
                            if (param.type.equals("QUERY")) {
                                out.print("RouteUtilsProvider.getQueryParam(exchange, \"" + param.name + "\", " + param.classType + ".class)");
                            } else if (param.type.equals("PATH")) {
                                out.print("RouteUtilsProvider.getPathParam(exchange, getAnnotationPath(), \"" + param.name + "\", " + param.classType + ".class)");
                            }
                            if (i < paramInfoList.size() - 1) {
                                out.print(",");
                            }
                            out.println();
                        }

                        out.println("            );");
                        out.println("         RouteUtilsProvider.processResponse(exchange, res, " + returnType + ".class);");
                        out.println("            } catch (IOException e) {");
                        out.println("                throw new RuntimeException(e);");
                        out.println("            }");
                        out.println("    });");

                    }
                    else{
                        out.println("        RouteUtilsProvider.withoutBody(exchange, () -> {");
                        out.println("            try {");
                        out.println("        com.sprintapi.core.di.DIContainer.getInstance(" + controllerFullName + ".class)");
                        out.println("            ." + methodName + "(");

                        for (int i = 0; i < paramInfoList.size(); i++) {
                            MethodParamInfo param = paramInfoList.get(i);
                            out.print("                ");
                            if (param.type.equals("QUERY")) {
                                out.print("RouteUtilsProvider.getQueryParam(exchange, \"" + param.name + "\", " + param.classType + ".class)");
                            } else if (param.type.equals("PATH")) {
                                out.print("RouteUtilsProvider.getPathParam(exchange, getAnnotationPath(), \"" + param.name + "\", " + param.classType + ".class)");
                            }
                            if (i < paramInfoList.size() - 1) {
                                out.print(",");
                            }
                            out.println();
                        }

                        out.println("            );");
                        out.println("                RouteUtilsProvider.processVoidResponse(exchange);");
                        out.println("            } catch (IOException e) {");
                        out.println("                throw new RuntimeException(e);");
                        out.println("            }");
                        out.println("    });");
                    }
                }

                out.println("    }");
                out.println("}");
            }
        } catch (IOException e) {
            System.out.println("Exception occured while generating Route class: " + e.getMessage());
        }

        return packageName + '.' + className;
    }
}
