package com.flightontime.api.dto;

import com.flightontime.api.model.FlightStatus;

/**
 * DTO para devolver el resultado de una sola predicción.
 */
public record PredictionResponse(
        FlightStatus estado,
        Double probabilidad
) {
}
