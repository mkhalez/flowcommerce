package com.coworking.space.orderservice.exceptionhandlers;

import com.coworking.space.orderservice.domain.exceptions.*;
import com.coworking.space.orderservice.dto.response.ErrorResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String NOT_HAVE_PERMITION = "access denied: You don't have permission to perform this operation";
    private static final String INVALID_OR_EXPIRED_TOKEN = "authentication failed: Invalid or expired token";
    private static final String UNEXPECTED_ERROR = "unexpected error occurred";
    private static final String EXTERNAL_SERVICE_UNAVAILABLE = "downstream service is temporarily unavailable, please try again later";
    private static final String NOT_SUPPORTED_ROUTE_ERROR = "route is not supported";


    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleRestClientException(RestClientException e) {
        log.atError().setCause(e).log();

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({
            ItemNotFoundException.class,
            OrderNotFoundException.class,
            OrderStatusIsNotCreated.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponse(NOT_SUPPORTED_ROUTE_ERROR));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.atWarn().log("Access denied: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(NOT_HAVE_PERMITION));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.atWarn().log("Authentication failed: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(INVALID_OR_EXPIRED_TOKEN));
    }

    @ExceptionHandler({
            CallNotPermittedException.class,
            ResourceAccessException.class
    })
    public ResponseEntity<ErrorResponse> handleUserServiceUnavailable(Exception e) {
        log.error("user-service unavailable", e);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(EXTERNAL_SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(UNEXPECTED_ERROR));
    }
}
