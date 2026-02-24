package com.ankap.platform.marketplace.api;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  record ErrorResponse(String code, String message, String path, Instant timestamp) {}

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException ex,
                                           jakarta.servlet.http.HttpServletRequest req) {
    return ResponseEntity.badRequest().body(
        new ErrorResponse("BAD_REQUEST", ex.getMessage(), req.getRequestURI(), Instant.now())
    );
  }

  @ExceptionHandler(IllegalStateException.class)
  ResponseEntity<ErrorResponse> conflict(IllegalStateException ex,
                                        jakarta.servlet.http.HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
        new ErrorResponse("CONFLICT", ex.getMessage(), req.getRequestURI(), Instant.now())
    );
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponse> fallback(Exception ex,
                                        jakarta.servlet.http.HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        new ErrorResponse("INTERNAL_ERROR", "unexpected error", req.getRequestURI(), Instant.now())
    );
  }
}