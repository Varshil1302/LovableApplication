package com.example.demo.exceptionHandling;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.MethodArgumentNotValidException;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}
