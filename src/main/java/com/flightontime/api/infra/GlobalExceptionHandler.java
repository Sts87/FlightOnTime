package com.flightontime.api.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDTO>> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Convierte el error feo de Java en una lista de errores clara

        List<FieldError> fieldErrors = ex.getFieldErrors();

        var errores = ex.getFieldErrors().stream()
                .map(ValidationErrorDTO::new)
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    // DTO pequeño interno para el formato de error
    private record ValidationErrorDTO(String campo, String mensaje) {
        public ValidationErrorDTO(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

}