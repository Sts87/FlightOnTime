package com.flightontime.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para devolver una fila de predicción completa (input + resultado) al cliente.
 */
public record BatchPredictionResultDTO(
        String aerolinea,
        String origen,
        String destino,
        LocalDateTime fechaDePartida,
        Integer distancia,
        String estadoPredicho,
        Double probabilidad
) {
}
