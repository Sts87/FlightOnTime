package com.flightontime.api.dto;

import com.flightontime.api.model.Flight;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * DTO para las últimas predicciones realizadas.
 */
public record RecentPredictionDTO(
        String aerolinea,
        String origen,
        String destino,
        Double porcentajeConfianza,
        String estado,
        LocalDateTime fechaConsulta
) {
    /**
     * Constructor desde la entidad Flight.
     */
    public RecentPredictionDTO(Flight flight) {
        this(
                flight.getAerolinea(),
                flight.getOrigen(),
                flight.getDestino(),
                convertirAPorcentaje(flight.getProbabilidad()),
                flight.getEstado().name(),
                flight.getFechaDePartida()
        );
    }

    /**
     * Convierte una probabilidad (0-1) a porcentaje (0-100) con 2 decimales.
     */
    private static Double convertirAPorcentaje(Double probabilidad) {
        if (probabilidad == null) return 0.0;

        return BigDecimal.valueOf(probabilidad)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}