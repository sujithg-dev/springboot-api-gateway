package com.springbootapigateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationErrors(MethodArgumentNotValidException ex)
    {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value() + " - " + HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "message", message
                ));
    }
}