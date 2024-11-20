package com.example.apibebakids.model.mysql;
import java.util.List;

public class LocalApiResponse<T> {
    private boolean Success;
    private String Message;
    private T data;
    private List<String> errors;

    public LocalApiResponse(boolean success, String message, T data, List<String> errors) {
        this.Success = success;
        this.Message = message;
        this.data = data;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        this.Success = success;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
