package com.app.trackingnumbergenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(ConstraintViolationException ex,
                                                                    HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = null;
            for (Path.Node node : violation.getPropertyPath()) {
                fieldName = node.getName();
            }
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(new ErrorResponse(400, "Bad Request",
                request.getServletPath(), errors), HttpStatus.BAD_REQUEST);
    }
}

@Data
@AllArgsConstructor
class ErrorResponse {

    private int status;
    private String message;
    private String path;
    private Map<String, String> errors;
}