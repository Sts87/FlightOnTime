package com.flightontime.api.infra;

import com.flightontime.api.exception.*;
import com.flightontime.api.service.CsvService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDTO>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        var errores = ex.getFieldErrors().stream()
                .map(ValidationErrorDTO::new)
                .toList();

        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(ModelLoadException.class)
    public ResponseEntity<ErrorResponseDTO> handleModelLoadException(ModelLoadException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "ERROR_CARGA_MODELO",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(PredictionException.class)
    public ResponseEntity<ErrorResponseDTO> handlePredictionException(PredictionException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "ERROR_PREDICCION",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(FeatureEngineeringException.class)
    public ResponseEntity<ErrorResponseDTO> handleFeatureEngineeringException(
            FeatureEngineeringException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "ERROR_PROCESAMIENTO_FEATURES",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(CsvParsingException.class)
    public ResponseEntity<?> handleCsvParsingException(CsvParsingException ex) {

        // Si tiene errores detallados, devolver respuesta estructurada
        if (ex.hasDetailedErrors()) {
            List<CsvErrorDetail> errorDetails = ex.getErrors().stream()
                    .map(error -> new CsvErrorDetail(
                            error.getLineNumber(),
                            error.getError(),
                            error.getLineContent()
                    ))
                    .collect(Collectors.toList());

            CsvErrorResponseDTO response = new CsvErrorResponseDTO(
                    "ERROR_VALIDACION_CSV",
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value(),
                    errorDetails
            );

            return ResponseEntity.badRequest().body(response);
        } else {
            // Error genérico de CSV
            ErrorResponseDTO error = new ErrorResponseDTO(
                    "ERROR_PARSING_CSV",
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST.value()
            );
            return ResponseEntity.badRequest().body(error);
        }
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidFileException(InvalidFileException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "ARCHIVO_INVALIDO",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "ARCHIVO_DEMASIADO_GRANDE",
                "El archivo excede el tamaño máximo permitido",
                HttpStatus.PAYLOAD_TOO_LARGE.value()
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(
            ResponseStatusException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "ERROR",
                ex.getReason() != null ? ex.getReason() : ex.getMessage(),
                ex.getStatusCode().value()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "ERROR_INTERNO",
                "Ha ocurrido un error inesperado. Por favor, contacte al administrador.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        // Log del error completo para debugging
        System.err.println("Error inesperado: " + ex.getMessage());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ===== DTOs internos =====

    private record ValidationErrorDTO(String campo, String mensaje) {
        public ValidationErrorDTO(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    private record ErrorResponseDTO(
            String codigo,
            String mensaje,
            int status,
            LocalDateTime timestamp
    ) {
        public ErrorResponseDTO(String codigo, String mensaje, int status) {
            this(codigo, mensaje, status, LocalDateTime.now());
        }
    }

    private record CsvErrorDetail(
            int lineaNumero,
            String error,
            String contenidoLinea
    ) {}

    private record CsvErrorResponseDTO(
            String codigo,
            String mensaje,
            int status,
            List<CsvErrorDetail> errores,
            LocalDateTime timestamp
    ) {
        public CsvErrorResponseDTO(String codigo, String mensaje, int status, List<CsvErrorDetail> errores) {
            this(codigo, mensaje, status, errores, LocalDateTime.now());
        }
    }
}