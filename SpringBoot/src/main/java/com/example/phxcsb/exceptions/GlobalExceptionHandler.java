package com.example.phxcsb.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        if (ex instanceof ExceptionOsalejaPuudub) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERR: " + ex.getMessage());
        }

        if (ex instanceof ExceptionEdetabelPuudub) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERR: " + ex.getMessage());
        }

        if (ex == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERR: Midagi läks valesti.");
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERR: " + ex.getMessage());
        }
    }
}
