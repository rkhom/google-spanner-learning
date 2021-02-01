package com.rkhom.spanner.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> handle(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
  }

  /**
   * Handles request body validation exceptions
   *
   * @param ex an exception which was thrown during validation
   * @return a list of invalid fields with description
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handle(MethodArgumentNotValidException ex) {
    String errors = ex.getBindingResult().getAllErrors().stream()
        .map(error -> String.format("%s%s: %s",
            error.getObjectName(),
            error instanceof FieldError ? "." + ((FieldError) error).getField() : "",
            error.getDefaultMessage()))
        .collect(Collectors.joining());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

}
