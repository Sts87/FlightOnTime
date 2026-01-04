package com.flightontime.api.dto;

import com.flightontime.api.model.FlightStatus;
import com.flightontime.api.validation.ValidAirline;
import com.flightontime.api.validation.ValidAirport;
import com.flightontime.api.validation.ValidationConstants;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO para datos de vuelo, ingresados por el usuario final o cliente.
 * Incluye validaciones completas para todos los campos requeridos.
 */
public record FlightData(
        @NotBlank(message = "La aerolínea es obligatoria")
        @Pattern(
                regexp = ValidationConstants.AIRLINE_CODE_PATTERN,
                message = "El código de aerolínea debe ser de 2 o 3 caracteres alfanuméricos (ej. AS, 5X)"
        )
        @ValidAirline
        String aerolinea,

        @NotBlank(message = "El origen no puede estar vacío")
        @Pattern(
                regexp = ValidationConstants.AIRPORT_CODE_PATTERN,
                message = "El código de origen debe ser de 3 letras mayúsculas (ej. ANC)"
        )
        @ValidAirport
        String origen,

        @NotBlank(message = "El destino es obligatorio")
        @Pattern(
                regexp = ValidationConstants.AIRPORT_CODE_PATTERN,
                message = "El código de destino debe ser de 3 letras mayúsculas (ej. SEA)"
        )
        @ValidAirport
        String destino,

        @NotNull(message = "La fecha de partida es requerida")
        @FutureOrPresent(message = "La fecha no puede ser del pasado")
        LocalDateTime fechaDePartida,

        @NotNull(message = "La distancia es obligatoria")
        @Positive(message = "La distancia debe ser un número positivo")
        @Min(
                value = ValidationConstants.MIN_DISTANCE_REQUIRED,
                message = "La distancia mínima de vuelo debe ser de {value} millas"
        )
        Integer distancia
) {
}