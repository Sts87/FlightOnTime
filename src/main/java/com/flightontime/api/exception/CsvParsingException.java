package com.flightontime.api.exception;

import com.flightontime.api.service.CsvService;

import java.util.Collections;
import java.util.List;

public class CsvParsingException extends RuntimeException {

    private final List<CsvService.CsvLineError> errors;

    public CsvParsingException(String message) {
        super(message);
        this.errors = Collections.emptyList();
    }

    public CsvParsingException(String message, Throwable cause) {
        super(message, cause);
        this.errors = Collections.emptyList();
    }

    public CsvParsingException(String message, List<CsvService.CsvLineError> errors) {
        super(message);
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public List<CsvService.CsvLineError> getErrors() {
        return errors;
    }

    public boolean hasDetailedErrors() {
        return !errors.isEmpty();
    }
}
