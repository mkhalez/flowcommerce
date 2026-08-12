package com.coworking.space.authenticationservice.exceptionhandles;

import com.coworking.space.authenticationservice.domain.exceptions.DisableUserException;
import com.coworking.space.authenticationservice.domain.exceptions.RefreshTokenInvalidOrExpiredException;
import com.coworking.space.authenticationservice.domain.exceptions.RoleNotFoundException;
import com.coworking.space.authenticationservice.domain.exceptions.UserAlreadyExistException;
import com.coworking.space.authenticationservice.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR = "internal server error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationException.class,
            RefreshTokenInvalidOrExpiredException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({
            RoleNotFoundException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(DisableUserException.class)
    public ResponseEntity<ErrorResponse> handleDisableUserException(DisableUserException e) {
        log.atError().setCause(e).log();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage()));
    }

}
