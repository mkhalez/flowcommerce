package com.coworking.space.userservice.exceptionhandler;

import com.coworking.space.userservice.exception.CardNotFoundException;
import com.coworking.space.userservice.exception.ExceededLimitException;
import com.coworking.space.userservice.exception.UserAlreadyExistException;
import com.coworking.space.userservice.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.coworking.space.userservice.dto.responses.ErrorResponse;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final String CARD_ALREADY_EXIST = "card with this number already exists";
    private static final String USER_WITH_EMAIL_ALREADY_EXIST = "user with this email already exists";
    private static final String PAYMENT_CARDS_NUMBER_KEY = "payment_cards_number_key";
    private static final String USERS_EMAIL_KEY = "users_email_key";
    private static final String NOT_HAVE_PERMITION = "access denied: You don't have permission to perform this operation";
    private static final String INVALID_OR_EXPIRED_TOKEN = "authentication failed: Invalid or expired token";
    private static final String UNEXPECTED_ERROR = "unexpected error occurred";

    @ExceptionHandler(ExceededLimitException.class)
    public ResponseEntity<ErrorResponse> handleExceededLimitException(Exception e) {
        log.atError().setCause(e).log();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({
            CardNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.atError().setCause(e).log();

        String rootErrorMessage = e.getMessage();
        String errorMessage = e.getMostSpecificCause().getMessage();

        if(rootErrorMessage.contains(PAYMENT_CARDS_NUMBER_KEY)) {
            errorMessage = CARD_ALREADY_EXIST;
        } else if (rootErrorMessage.contains(USERS_EMAIL_KEY)) {
            errorMessage = USER_WITH_EMAIL_ALREADY_EXIST;
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(errorMessage));
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(UNEXPECTED_ERROR));
    }
}
