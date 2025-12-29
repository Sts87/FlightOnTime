package com.flightontime.api.dto;

import com.flightontime.api.model.FlightStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record FlightData(
        @NotBlank(message = "La aerolínea es obligatoria")
        String aerolinea,

        @NotBlank(message = "El origen no puede estar vacío")
        String origen,

        @NotBlank(message = "El destino es obligatorio")
        String destino,

        @NotNull(message = "La fecha de partida es requerida")
        @FutureOrPresent(message = "La fecha no puede ser del pasado")
        LocalDateTime fechaDePartida,

        @NotBlank
        @Positive(message = "La distancia debe ser un número positivo")
        Integer distancia,

        @NotNull(message = "El estado del vuelo es obligatorio")
        FlightStatus estado
) {
}
