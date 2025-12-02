package com.bookstore.backend.exception;

import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("status", 400, "error", "Bad Request", "message", ex.getMessage()));
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("status", 404, "error", "Not Found", "message", ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("status", 409, "error", "Conflict", "message", ex.getMessage()));
  }

  // Tất cả lỗi khác (fallback)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("status", 500, "error", "Internal Server Error", "message", ex.getMessage()));
  }
}
