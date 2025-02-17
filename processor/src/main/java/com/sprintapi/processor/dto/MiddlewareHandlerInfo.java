package com.sprintapi.processor.dto;

public class MiddlewareHandlerInfo {
    private String className;
    private Integer order;

    public MiddlewareHandlerInfo(String className, Integer order) {
        this.className = className;
        this.order = order;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
