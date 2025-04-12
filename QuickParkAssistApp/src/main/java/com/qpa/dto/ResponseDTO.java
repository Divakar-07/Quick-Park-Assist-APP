package com.qpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null values from JSON response
public class ResponseDTO<T> { 

    @JsonProperty("message") // Ensures correct field mapping in JSON
    private String message;

    @JsonProperty("status")
    private int status;

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("data")
    private T data;

    // Constructors
    public ResponseDTO(String message, int status, boolean success, T data) {
        this.message = message;
        this.status = status;
        this.success = success;
        this.data = data;
    }

    public ResponseDTO(String message, int status, boolean success) {
        this(message, status, success, null);
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
