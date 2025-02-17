package com.sprintapi.processor;

import com.google.auto.service.AutoService;
import com.sprintapi.processor.annotations.Inject;
import com.sprintapi.processor.annotations.Middleware;
import com.sprintapi.processor.annotations.RestController;
import com.sprintapi.processor.dto.MethodParamInfo;
import com.sprintapi.processor.dto.MiddlewareHandlerInfo;
import com.sprintapi.processor.generators.DIContainerGenerator;
import com.sprintapi.processor.generators.MiddleWareRegistryGenerator;
import com.sprintapi.processor.generators.RouteGenerator;
import com.sprintapi.processor.generators.RouteRegistryGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "com.sprintapi.processor.annotations.Component",
        "com.sprintapi.processor.annotations.Service",
        "com.sprintapi.processor.annotations.Repository",
        "com.sprintapi.processor.annotations.RestController",
        "com.sprintapi.processor.annotations.Middleware",
        "com.sprintapi.processor.annotations.Get",
        "com.sprintapi.processor.annotations.Post",
        "com.sprintapi.processor.annotations.Put",
        "com.sprintapi.processor.annotations.Delete",
        "com.sprintapi.processor.annotations.QueryParam",
        "com.sprintapi.processor.annotations.PathParam",
        "com.sprintapi.processor.annotations.Body"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SprintAnnotationProcessor extends AbstractProcessor {
    private final Map<String, String> componentClasses = new HashMap<>();
    private final Map<String, List<String>> constructorParams = new HashMap<>();
    private final List<String> routeEntries = new ArrayList<>();
    private final List<MiddlewareHandlerInfo> middlewares = new ArrayList<>();
    private boolean processed = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processed) return true;
        for (Element element : roundEnv.getRootElements()) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;

                if (isComponent(typeElement)) {
                    String className = typeElement.getQualifiedName().toString();
                    String simpleName = typeElement.getSimpleName().toString();
                    componentClasses.put(className, simpleName);

                    List<String> paramTypes = new ArrayList<>();
                    for (Element enclosed : typeElement.getEnclosedElements()) {
                        if (enclosed.getKind() == ElementKind.CONSTRUCTOR && enclosed.getAnnotation(Inject.class) != null) {
                            ExecutableElement constructor = (ExecutableElement) enclosed;
                            for (VariableElement param : constructor.getParameters()) {
                                paramTypes.add(param.asType().toString());
                            }
                            break; // Only one @Inject constructor is allowed
                        }
                    }
                    constructorParams.put(className, paramTypes);
                }

                if (typeElement.getAnnotation(RestController.class) != null) {
                    processRoutes(typeElement);
                }

                if(isMiddleware(typeElement)){
                    String className = typeElement.getQualifiedName().toString();
                    Middleware middlewareAnnotation = typeElement.getAnnotation(Middleware.class);
                    int order = Integer.MAX_VALUE;
                    if (middlewareAnnotation != null) {
                        order = middlewareAnnotation.order();
                        System.out.println("Middleware Order: " + order);
                    }
                    middlewares.add(new MiddlewareHandlerInfo(className, order));
                }
            }
        }

        System.out.println("Detected components: " + componentClasses);
        DIContainerGenerator.generateDIContainer(processingEnv, componentClasses, constructorParams);
        MiddleWareRegistryGenerator.generateMiddlewareRegistry(processingEnv, middlewares);
        RouteRegistryGenerator.generateRouteRegistry(processingEnv, routeEntries);

        processed = true;
        return true;
    }

    private boolean isComponent(TypeElement typeElement) {
        for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
            String annotationName = annotationMirror.getAnnotationType().toString();
            if (annotationName.equals("com.sprintapi.processor.annotations.Component") ||
                    annotationName.equals("com.sprintapi.processor.annotations.Service") ||
                    annotationName.equals("com.sprintapi.processor.annotations.Repository") ||
                    annotationName.equals("com.sprintapi.processor.annotations.Middleware") ||
                    annotationName.equals("com.sprintapi.processor.annotations.RestController")) {
                return true;
            }
        }
        return false;
    }

    private boolean isMiddleware(TypeElement typeElement) {
        for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
            String annotationName = annotationMirror.getAnnotationType().toString();
            if (annotationName.equals("com.sprintapi.processor.annotations.Middleware")) {
                return true;
            }
        }
        return false;
    }

    private void processRoutes(TypeElement controller) {
        String controllerClass = controller.getQualifiedName().toString();
        String controllerSimpleName = controller.getSimpleName().toString();

        for (Element enclosed : controller.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) enclosed;
                String methodName = method.getSimpleName().toString();

                TypeMirror returnType = method.getReturnType();
                String returnTypeStr = returnType.toString();

                for (AnnotationMirror annotation : method.getAnnotationMirrors()) {
                    String annotationType = annotation.getAnnotationType().toString();
                    String httpMethod = null;
                    String path = "";

                    switch (annotationType) {
                        case "com.sprintapi.processor.annotations.Get" -> httpMethod = "GET";
                        case "com.sprintapi.processor.annotations.Post" -> httpMethod = "POST";
                        case "com.sprintapi.processor.annotations.Put" -> httpMethod = "PUT";
                        case "com.sprintapi.processor.annotations.Delete" -> httpMethod = "DELETE";
                    }

                    if (httpMethod != null) {
                        for (ExecutableElement key : annotation.getElementValues().keySet()) {
                            if (key.getSimpleName().toString().equals("value")) {
                                path = annotation.getElementValues().get(key).toString().replace("\"", "");
                            }
                        }

                        List<MethodParamInfo> parameters = new ArrayList<>();
                        String bodyParamClassName = "Object";
                        boolean isBodyExist = false;
                        for (VariableElement param : method.getParameters()) {
                            String paramType = "";
                            String paramValue = ""; // Extracted value from annotation
                            String classType = "";

                            for (AnnotationMirror paramAnnotationMirror : param.getAnnotationMirrors()) {
                                String paramAnnotationType = paramAnnotationMirror.getAnnotationType().toString();

                                switch (paramAnnotationType) {
                                    case "com.sprintapi.processor.annotations.QueryParam" -> {
                                        paramType = "QUERY";
                                        TypeMirror typeMirror = param.asType();
                                        TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
                                        classType = typeElement.getQualifiedName().toString();
                                    }
                                    case "com.sprintapi.processor.annotations.PathParam" -> {
                                        paramType = "PATH";
                                        TypeMirror typeMirror = param.asType();
                                        TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
                                        classType = typeElement.getQualifiedName().toString();
                                    }
                                    case "com.sprintapi.processor.annotations.Body" -> {
                                        paramType = "BODY";
                                        TypeMirror typeMirror = param.asType();
                                        TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
                                        bodyParamClassName = typeElement.getQualifiedName().toString();
                                        isBodyExist = true;
                                    }
                                }

                                // Extract the annotation value (e.g., @PathParam("id") -> "id")
                                for (ExecutableElement key : paramAnnotationMirror.getElementValues().keySet()) {
                                    if (key.getSimpleName().toString().equals("value")) {
                                        paramValue = paramAnnotationMirror.getElementValues().get(key).toString().replace("\"", "");
                                    }
                                }
                            }
                            parameters.add(new MethodParamInfo(paramType, paramValue, classType));
                        }
                        routeEntries.add(RouteGenerator.generateRouteClass(processingEnv, controllerClass,controllerSimpleName, methodName, path, httpMethod, parameters, bodyParamClassName, isBodyExist, returnTypeStr));
                    }
                }
            }
        }
    }
}

