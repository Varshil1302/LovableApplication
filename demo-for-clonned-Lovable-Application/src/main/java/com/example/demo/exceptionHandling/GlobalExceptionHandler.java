package com.example.demo.exceptionHandling;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.MethodArgumentNotValidException;
import com.example.demo.exception.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex)
    {
        ApiError apiError= new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex)
    {
        ApiError apiError= new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex)
    {
        ApiError apiError= new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Username not found with username: "+ex.getMessage());
       // log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
       // log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid JWT token: " + ex.getMessage());
       // log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = new ApiError( HttpStatus.FORBIDDEN, "Access denied: Insufficient permissions");
       // log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

}
