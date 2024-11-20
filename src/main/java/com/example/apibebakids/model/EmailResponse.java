package com.example.apibebakids.model;

import java.util.List;

public class EmailResponse<T> {
    private boolean responseResult;
    private String errorMessage;
    private int respResultCount;
    private T data;

    // Getters and setters
    public boolean isResponseResult() {
        return responseResult;
    }

    public void setResponseResult(boolean responseResult) {
        this.responseResult = responseResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRespResultCount() {
        return respResultCount;
    }

    public void setRespResultCount(int respResultCount) {
        this.respResultCount = respResultCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}