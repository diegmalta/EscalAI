package com.ufrj.escalaiv2.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int httpStatusCode;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
