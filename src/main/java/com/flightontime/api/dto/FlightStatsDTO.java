package com.flightontime.api.dto;

import java.time.LocalDate;

/**
 * DTO para estadísticas de vuelos.
 */
public record FlightStatsDTO(
        LocalDate fecha,
        Long totalConsultas,
        Long vuelosRetrasados,
        Long vuelosPuntuales,
        Double porcentajeRetrasados,
        Double porcentajePuntuales,
        Double probabilidadPromedio
) {
    /**
     * Constructor de conveniencia para crear estadísticas con cálculos automáticos.
     */
    public FlightStatsDTO(LocalDate fecha, Long totalConsultas, Long vuelosRetrasados, Double probabilidadPromedio) {
        this(
                fecha,
                totalConsultas,
                vuelosRetrasados,
                totalConsultas - vuelosRetrasados,
                totalConsultas > 0 ? (vuelosRetrasados * 100.0 / totalConsultas) : 0.0,
                totalConsultas > 0 ? ((totalConsultas - vuelosRetrasados) * 100.0 / totalConsultas) : 0.0,
                probabilidadPromedio
        );
    }
}
