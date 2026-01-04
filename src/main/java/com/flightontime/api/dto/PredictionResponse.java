package com.flightontime.api.dto;

import com.flightontime.api.model.FlightStatus;

public record PredictionResponse(
        FlightStatus estado,
        Double probabilidad
) {
}
