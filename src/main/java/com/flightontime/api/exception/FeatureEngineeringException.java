package com.flightontime.api.exception;

public class FeatureEngineeringException extends RuntimeException {
    public FeatureEngineeringException(String message) {
        super(message);
    }

    public FeatureEngineeringException(String message, Throwable cause) {
        super(message, cause);
    }
}
