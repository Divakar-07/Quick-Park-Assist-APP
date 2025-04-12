/**
 * Exception thrown to indicate that the provided credentials are invalid.
 * This is a custom runtime exception that can be used to handle authentication
 * or authorization errors in the application.
 *
 * @author [Your Name]
 * @version 1.0
 */
package com.qpa.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
