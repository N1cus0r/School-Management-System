package com.example.schoolmanagementsystem.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private ApiError generateApiError(
            String requestUri,
            String errorMessage,
            Integer statusCode
    ) {
        return new ApiError(
                requestUri,
                errorMessage,
                statusCode,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleServerError(
            Exception exception,
            HttpServletRequest request
    ) {
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(
                generateApiError(
                        request.getRequestURI(),
                        exception.getMessage(),
                        responseStatus.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadableRequestError(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        Throwable rootCause = exception.getRootCause();

        InvalidFormatException invalidFormatException = (InvalidFormatException) rootCause;

        String fieldPath = invalidFormatException.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        String errorMessage = "Invalid value provided for field '" + fieldPath;

        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                generateApiError(
                        request.getRequestURI(),
                        errorMessage,
                        responseStatus.value()),
                responseStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleRequestFieldsValidationError(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errorMessages = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errorMessages.put(error.getField(), error.getDefaultMessage()));

        ValidationError validationError = new ValidationError(
                request.getRequestURI(),
                errorMessages,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InsufficientAuthenticationException.class, NotEnoughAuthorityException.class})
    public ResponseEntity<ApiError> handleAuthorizationError(
            Exception exception,
            HttpServletRequest request
    ) {
        HttpStatus responseStatus = HttpStatus.FORBIDDEN;

        return new ResponseEntity<>(
                generateApiError(
                        request.getRequestURI(),
                        exception.getMessage(),
                        responseStatus.value()),
                responseStatus);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleAuthenticationError(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {
        HttpStatus responseStatus = HttpStatus.UNAUTHORIZED;

        return new ResponseEntity<>(
                generateApiError(
                        request.getRequestURI(),
                        exception.getMessage(),
                        responseStatus.value()),
                responseStatus);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundError(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        HttpStatus responseStatus = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
                generateApiError(
                        request.getRequestURI(),
                        exception.getMessage(),
                        responseStatus.value()),
                responseStatus);
    }

    @ExceptionHandler({RequestValidationError.class, UserEmailTakeException.class})
    public ResponseEntity<ApiError> handleRequestValidationError(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(
                generateApiError(
                        request.getRequestURI(),
                        exception.getMessage(),
                        responseStatus.value()),
                responseStatus);
    }
}
