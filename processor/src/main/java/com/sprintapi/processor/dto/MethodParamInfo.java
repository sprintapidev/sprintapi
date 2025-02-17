package com.sprintapi.processor.dto;

public class MethodParamInfo {
    public String type;
    public String name;
    public String classType;

    public MethodParamInfo(String type, String name, String classType) {
        this.type = type;
        this.name = name;
        this.classType = classType;
    }
}
