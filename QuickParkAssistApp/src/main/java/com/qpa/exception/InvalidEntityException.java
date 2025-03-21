/**
 * Exception thrown to indicate that an entity is invalid.
 * This exception extends {@link RuntimeException}, allowing it to be used
 * for unchecked exceptions.
 * 
 * <p>Use this exception to signal that an entity does not meet the required
 * validation criteria or contains invalid data.
 * 
 * @param message A detailed message describing the reason for the exception.
 */
package com.qpa.exception;

public class InvalidEntityException extends RuntimeException {
    public InvalidEntityException(String message) {
        super(message);
    }
}