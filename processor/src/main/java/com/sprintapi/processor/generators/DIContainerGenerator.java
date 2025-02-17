package com.sprintapi.processor.generators;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class DIContainerGenerator {
    public static void generateDIContainer(ProcessingEnvironment processingEnv, Map<String, String> componentClasses, Map<String, List<String>> constructorParams) {
        String className = "DIContainer";
        String packageName = "com.sprintapi.core.di";

        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + className);
            try (PrintWriter out = new PrintWriter(file.openWriter())) {
                out.println("package " + packageName + ";");
                out.println("import java.util.HashMap;");
                out.println("import java.util.Map;");

                // Collect unique imports
                Set<String> imports = new HashSet<>();
                imports.addAll(componentClasses.keySet());
                imports.addAll(constructorParams.values().stream().flatMap(List::stream).toList());

                // Print imports
                for (String importClass : imports) {
                    out.println("import " + importClass + ";");
                }

                out.println("public class " + className + " {");
                out.println("    private static final Map<Class<?>, Object> instances = new HashMap<>();");

                // Recursive method for creating instances
                out.println("    private static <T> T createInstance(Class<T> clazz) {");
                out.println("        if (instances.containsKey(clazz)) {");
                out.println("            return clazz.cast(instances.get(clazz));");
                out.println("        }");

                out.println("        try {");
                out.println("            Object instance;");

                out.println("            switch (clazz.getSimpleName()) {");

                for (Map.Entry<String, String> entry : componentClasses.entrySet()) {
                    String fullClassName = entry.getKey();
                    String simpleClassName = entry.getValue();
                    List<String> params = constructorParams.getOrDefault(fullClassName, new ArrayList<>());

                    out.println("                case \"" + simpleClassName + "\":");
                    if (params.isEmpty()) {
                        out.println("                    instance = new " + simpleClassName + "();");
                    } else {
                        out.print("                    instance = new " + simpleClassName + "(");
                        out.print(params.stream().map(p -> "createInstance(" + p + ".class)").collect(Collectors.joining(", ")));
                        out.println(");");
                    }
                    out.println("                    break;");
                }

                out.println("                default: throw new RuntimeException(\"Unknown dependency: \" + clazz.getName());");
                out.println("            }");

                out.println("            instances.put(clazz, instance);");
                out.println("            return clazz.cast(instance);");

                out.println("        } catch (Exception e) {");
                out.println("            throw new RuntimeException(\"Failed to create instance of \" + clazz.getName(), e);");
                out.println("        }");
                out.println("    }");

                // Static block to initialize dependencies correctly
                out.println("    static {");
                for (String simpleClassName : componentClasses.values()) {
                    out.println("        createInstance(" + simpleClassName + ".class);");
                }
                out.println("    }");

                // Method to get instances
                out.println("    public static <T> T getInstance(Class<T> clazz) {");
                out.println("        return createInstance(clazz);");
                out.println("    }");

                out.println("}");
            }
        } catch (IOException e) {
            System.out.println("Exception occured while generating DIConainer: " + e.getMessage());
        }
    }
}
