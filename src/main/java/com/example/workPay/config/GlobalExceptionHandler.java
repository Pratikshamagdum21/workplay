package com.example.workPay.config;

import com.example.workPay.entities.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<Map<String, String>> handleErrorResponse(ErrorResponse ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "errorCode", ex.getErrorCode(),
                        "message", ex.getMessage() != null ? ex.getMessage() : "An error occurred"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "errorCode", "500",
                        "message", "An unexpected server error occurred"
                ));
    }
}
