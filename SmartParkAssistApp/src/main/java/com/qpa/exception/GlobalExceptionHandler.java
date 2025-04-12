package com.qpa.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.qpa.dto.ResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Handles validation errors for input fields
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ResponseDTO<Map<String, String>>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(fieldError -> errors.put(fieldError.getField(),
                                                fieldError.getDefaultMessage()));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ResponseDTO<>("Validation failed", HttpStatus.BAD_REQUEST.value(), false,
                                                errors));
        }

        /**
         * Handles resource not found exceptions
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ResponseDTO<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseDTO<>(ex.getMessage(), HttpStatus.NOT_FOUND.value(), false));
        }

        /**
         * Handles invalid entity exceptions
         */
        @ExceptionHandler(InvalidEntityException.class)
        public ResponseEntity<ResponseDTO<Void>> handleEmployeeNotFoundException(InvalidEntityException ex) {
                Map<String, String> error = new HashMap<>();
                error.put("message", ex.getMessage());
                return ResponseEntity.badRequest()
                                .body(new ResponseDTO<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), false));
        }

        @ExceptionHandler(UnauthorizedAccessException.class)
        public ResponseEntity<ResponseDTO<Void>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ResponseDTO<>(ex.getMessage(), 401, false));
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ResponseDTO<Void>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ResponseDTO<>(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), false));
        }

}

/*
 * 
 * put by amit
 * package com.qpa.exception;
 * 
 * import java.util.HashMap;
 * import java.util.Map;
 * 
 * import org.springframework.http.HttpStatus;
 * import org.springframework.http.ResponseEntity;
 * import org.springframework.web.bind.MethodArgumentNotValidException;
 * import org.springframework.web.bind.annotation.ExceptionHandler;
 * import org.springframework.web.bind.annotation.RestControllerAdvice;
 * import org.springframework.web.multipart.MaxUploadSizeExceededException;
 * 
 * import com.qpa.dto.ResponseDTO;
 * 
 * @RestControllerAdvice
 * public class GlobalExceptionHandler {
 * // Handle validation errors for input fields
 * 
 * @ExceptionHandler(MethodArgumentNotValidException.class)
 * public ResponseEntity<ResponseDTO<Map<String, String>>>
 * handleValidationExceptions(
 * MethodArgumentNotValidException ex) {
 * Map<String, String> errors = new HashMap<>();
 * ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
 * errors.put(fieldError.getField(), fieldError.getDefaultMessage());
 * });
 * 
 * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
 * .body(new ResponseDTO<>("Validation failed", 400, false, errors));
 * }
 * 
 * // Handle cases where a requested entity (User/Vehicle) is not found
 * 
 * @ExceptionHandler(InvalidEntityException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleInvalidEntityException(InvalidEntityException ex) {
 * return ResponseEntity.status(HttpStatus.NOT_FOUND)
 * .body(new ResponseDTO<>(ex.getMessage(), 404, false));
 * }
 * 
 * // Handle file upload size exceeding limit
 * 
 * @ExceptionHandler(MaxUploadSizeExceededException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleMaxSizeException(MaxUploadSizeExceededException ex) {
 * return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
 * .body(new ResponseDTO<>
 * ("File size exceeds the maximum allowed limit. Please upload a smaller file."
 * ,
 * 413, false));
 * }
 * 
 * // Handle SQL exceptions (like duplicate entry errors)
 * 
 * @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.
 * class)
 * public ResponseEntity<ResponseDTO<Void>> handleSQLIntegrityViolation(
 * org.springframework.dao.DataIntegrityViolationException ex) {
 * return ResponseEntity.status(HttpStatus.CONFLICT)
 * .body(new ResponseDTO<>("Duplicate entry or constraint violation", 409,
 * false));
 * }
 * 
 * // Handle all other unhandled exceptions
 * 
 * @ExceptionHandler(Exception.class)
 * public ResponseEntity<ResponseDTO<Void>> handleGeneralException(Exception ex)
 * {
 * System.out.println(ex.getMessage());
 * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
 * .body(new ResponseDTO<>("An unexpected error occurred", 500, false));
 * }
 * 
 * @ExceptionHandler(InvalidVehicleTypeException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleInvalidVehicleType(InvalidVehicleTypeException ex) {
 * return ResponseEntity.status(HttpStatus.BAD_REQUEST)
 * .body(new ResponseDTO<>(ex.getMessage(), 400, false));
 * }
 * 
 * 
 * 
 * 
 * @ExceptionHandler(ResourceNotFoundException.class)
 * public ResponseEntity<ResponseDTO<Void>>
 * handleResourceNotFoundException(ResourceNotFoundException ex) {
 * 
 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
 * .body(new ResponseDTO<>(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(),
 * false));
 * }
 * }
 */