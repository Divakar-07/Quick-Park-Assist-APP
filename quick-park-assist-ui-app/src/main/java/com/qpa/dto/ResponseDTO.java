package com.qpa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ResponseDTO is a generic class used for sending structured responses.
 * It helps in standardizing API responses with a message, status code, success flag, and optional data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields from JSON response
public class ResponseDTO<T> {

    @JsonProperty("message") // Maps this field to "message" in the JSON response
    private String message;

    @JsonProperty("status") // Maps this field to "status" in the JSON response
    private int status;

    @JsonProperty("success") // Indicates if the operation was successful
    private boolean success;

    @JsonProperty("data") // Holds the response data of generic type T
    private T data;

    /**
     * Default constructor required for serialization and deserialization.
     */
    public ResponseDTO() {}

    /**
     * Constructor to initialize all fields.
     * @param message The response message.
     * @param status The HTTP status code.
     * @param success Indicates if the operation was successful.
     * @param data The response data.
     */
    public ResponseDTO(String message, int status, boolean success, T data) {
        this.message = message;
        this.status = status;
        this.success = success;
        this.data = data;
    }

    /**
     * Constructor to initialize response without data.
     * @param message The response message.
     * @param status The HTTP status code.
     * @param success Indicates if the operation was successful.
     */
    public ResponseDTO(String message, int status, boolean success) {
        this(message, status, success, null);
    }

    /**
     * Gets the response message.
     * @return The response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     * @param message The new message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the HTTP status code.
     * @return The status code.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status code.
     * @param status The new status code.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Checks if the operation was successful.
     * @return True if successful, otherwise false.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success flag.
     * @param success The new success state.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the response data.
     * @return The response data.
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the response data.
     * @param data The new response data.
     */
    public void setData(T data) {
        this.data = data;
    }
}
