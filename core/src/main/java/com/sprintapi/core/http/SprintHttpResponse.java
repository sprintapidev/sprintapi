package com.sprintapi.core.http;

import java.util.HashMap;
import java.util.Map;

public class SprintHttpResponse {
    private int statusCode;
    private Map<String, String> headers;
    private Object body;

    public SprintHttpResponse() {
    }

    public SprintHttpResponse(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", "application/json; charset=UTF-8");
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
