package com.lucianoak.experimentregistry.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
      EmailAlreadyExistsException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(ResearcherNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResearcherNotFound(
      ResearcherNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(WorkflowNotFountException.class)
  public ResponseEntity<ErrorResponse> handleWorkflowNotFound(
      WorkflowNotFountException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(DuplicateStatusException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateStatus(
      DuplicateStatusException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(ExperimentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleExperimentNotFound(
      ExperimentNotFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(NoStatusAssociatedWithWorkflowException.class)
  public ResponseEntity<ErrorResponse> hendleNoStatusAssociatedWithWorkflow(
      NoStatusAssociatedWithWorkflowException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(DuplicateExperimentTitleException.class)
  public ResponseEntity<ErrorResponse> handleDublicateExperimentTitle(
      DuplicateExperimentTitleException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(ParameterAlreadyExistsInExperimentException.class)
  public ResponseEntity<ErrorResponse> handleParameterAlreadyExistsInExperiment(
      ParameterAlreadyExistsInExperimentException e) {
    HttpStatus status = HttpStatus.CONFLICT;
    return ResponseEntity.status(status).body(new ErrorResponse(status, e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity.status(status).body(new ErrorResponse(status,
        "Invalid value for parameter '" + e.getName() + "'"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();

    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    HttpStatus status = HttpStatus.BAD_REQUEST;

    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(
            status,
            "Validation failed",
            errors));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
      MissingServletRequestParameterException e) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(
            status,
            "Required parameter '" + e.getParameterName() + "' is missing"));
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleMethodValidation(
      HandlerMethodValidationException e) {
    Map<String, String> errors = new HashMap<>();

    e.getValueResults().forEach(result -> {
      String parameterName = result.getMethodParameter().getParameterName();

      result.getResolvableErrors().forEach(error -> {
        String message = error.getDefaultMessage();

        if (parameterName != null && message != null) {
          errors.put(parameterName, message);
        }
      });
    });

    HttpStatus status = HttpStatus.BAD_REQUEST;

    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(
            status,
            "Validation failed",
            errors));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFound(
      NoResourceFoundException e) {
    HttpStatus status = HttpStatus.NOT_FOUND;

    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(
            status,
            "Resource not found"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(
      Exception e) {
    log.error("Unexpected error", e);

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    return ResponseEntity
        .status(status)
        .body(new ErrorResponse(
            status,
            "An unexpected error occurred"));
  }
}
