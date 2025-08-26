package com.worbes.adapter.web.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        List<ErrorResponse.FieldError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new ErrorResponse.FieldError(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());
        ErrorResponse errorResponse = new ErrorResponse(
                status.name(),
                "잘못된 요청입니다.",
                errors
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Invalid argument type: {}", ex.getName(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getName() + " 파라미터가 잘못되었습니다.";
        ErrorResponse errorResponse = new ErrorResponse(
                status.name(),
                message,
                null
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        log.warn("Missing request parameter: {}", ex.getParameterName());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                status.name(),
                ex.getParameterName() + " 파라미터가 필요합니다.",
                null
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Bad request", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorResponse = new ErrorResponse(
                status.name(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled server error", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                status.name(),
                "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                null
        );

        return ResponseEntity.status(status).body(errorResponse);
    }
}
