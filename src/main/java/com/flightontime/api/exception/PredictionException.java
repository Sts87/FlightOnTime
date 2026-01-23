package com.flightontime.api.exception;

public class PredictionException extends RuntimeException {
    public PredictionException(String message) {
        super(message);
    }

    public PredictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
