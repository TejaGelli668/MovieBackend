package com.example.adminbackend.dto;

import java.util.Map;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // Constructors
    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Static factory methods for error responses (used by GlobalExceptionHandler)
    public static ApiResponse<Object> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static ApiResponse<Map<String, String>> error(String message, Map<String, String> errors) {
        return new ApiResponse<>(false, message, errors);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

    // Static factory methods for success responses
    public static ApiResponse<Object> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}