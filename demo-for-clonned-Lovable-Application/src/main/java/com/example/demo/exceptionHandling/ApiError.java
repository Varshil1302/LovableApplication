package com.example.demo.exceptionHandling;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiError(
        HttpStatus httpStatus,
        String message,
        Instant timeStamp
) {

    public ApiError(HttpStatus httpStatus,String message)
    {
        this(httpStatus,message,Instant.now());
    }

}
