/**
 * Exception thrown to indicate that an unauthorized access attempt has occurred.
 * This is a runtime exception and can be used to signal security-related issues
 * such as access control violations.
 */
package com.qpa.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
