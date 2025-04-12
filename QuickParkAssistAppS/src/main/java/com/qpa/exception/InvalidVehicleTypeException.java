/**
 * Exception thrown to indicate that an invalid vehicle type has been provided.
 * This is a custom runtime exception used to handle errors related to vehicle type validation.
 */
package com.qpa.exception;

public class InvalidVehicleTypeException extends RuntimeException {
    public InvalidVehicleTypeException(String message) {
        super(message);
    }
}
