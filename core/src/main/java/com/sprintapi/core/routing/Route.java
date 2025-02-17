package com.sprintapi.core.routing;
import io.undertow.server.HttpServerExchange;
public abstract class Route {
    private String annotationPath;
    private String annotationMethodType;
    abstract public void invoke(HttpServerExchange exchange) throws Exception;

    public String getAnnotationPath() {
        return annotationPath;
    }

    public void setAnnotationPath(String annotationPath) {
        this.annotationPath = annotationPath;
    }

    public String getAnnotationMethodType() {
        return annotationMethodType;
    }

    public void setAnnotationMethodType(String annotationMethodType) {
        this.annotationMethodType = annotationMethodType;
    }
}
