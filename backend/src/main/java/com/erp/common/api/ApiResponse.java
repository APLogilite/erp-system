package com.erp.common.api;

import java.util.Collections;
import java.util.List;

public class ApiResponse<T> {

  private boolean success;
  private T data;
  private String message;
  private String errorCode;
  private List<ApiErrorDetail> details;

  public ApiResponse() {}

  public ApiResponse(boolean success, T data, String message, String errorCode, List<ApiErrorDetail> details) {
    this.success = success;
    this.data = data;
    this.message = message;
    this.errorCode = errorCode;
    this.details = details;
  }

  public static <T> ApiResponse<T> success(T data, String message) {
    return new ApiResponse<>(true, data, message, null, Collections.emptyList());
  }

  public static <T> ApiResponse<T> success(T data) {
    return success(data, null);
  }

  public static ApiResponse<Void> successMessage(String message) {
    return new ApiResponse<>(true, null, message, null, Collections.emptyList());
  }

  public static ApiResponse<Void> error(String errorCode, String message) {
    return new ApiResponse<>(false, null, message, errorCode, Collections.emptyList());
  }

  public static ApiResponse<Void> error(String errorCode, String message, List<ApiErrorDetail> details) {
    return new ApiResponse<>(false, null, message, errorCode, details);
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public List<ApiErrorDetail> getDetails() {
    return details;
  }

  public void setDetails(List<ApiErrorDetail> details) {
    this.details = details;
  }
}
