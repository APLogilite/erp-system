package com.erp.config;

import com.erp.common.api.ApiErrorDetail;
import com.erp.common.api.ApiResponse;
import com.erp.core.metadata.exception.MetadataNotFoundException;
import com.erp.core.metadata.exception.MetadataValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

  @ExceptionHandler(MetadataNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleMetadataNotFound(MetadataNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("METADATA_NOT_FOUND", exception.getMessage()));
  }

  @ExceptionHandler(MetadataValidationException.class)
  public ResponseEntity<ApiResponse<Void>> handleMetadataValidation(MetadataValidationException exception) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("METADATA_VALIDATION_ERROR", exception.getMessage()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("NOT_FOUND", exception.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("VALIDATION_ERROR", exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
    String message = String.format("Invalid value '%s' for parameter '%s'.", exception.getValue(), exception.getName());
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("TYPE_MISMATCH", message));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException exception) {
    var details = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> new ApiErrorDetail(error.getField(), error.getDefaultMessage()))
        .collect(Collectors.toList());

    return ResponseEntity.badRequest()
        .body(ApiResponse.error("VALIDATION_FAILED", "One or more fields failed validation.", details));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception exception) {
    log.error("Unhandled exception: {}", exception.getMessage(), exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred. Please contact support."));
  }
}

