package com.flightontime.api.dto;

import com.flightontime.api.model.FlightStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FlightData(
        @NotBlank String aerolinea,
        @NotBlank String origen,
        @NotBlank String destino,
        @NotNull LocalDateTime fechaDePartida,
        @NotNull Integer distancia,
        @NotNull FlightStatus estado
) {
}
