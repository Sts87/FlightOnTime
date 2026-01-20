package com.flightontime.api.service;

import com.flightontime.api.dto.FlightStatsDTO;
import com.flightontime.api.model.FlightStatus;
import com.flightontime.api.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Servicio para calcular estadísticas de vuelos.
 */
@Service
public class StatsService {

    private final FlightRepository flightRepository;

    public StatsService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Obtiene estadísticas de vuelos para hoy.
     */
    @Transactional(readOnly = true)
    public FlightStatsDTO getStatsForToday() {
        LocalDate today = LocalDate.now();
        return getStatsForDate(today);
    }

    /**
     * Obtiene estadísticas de vuelos para una fecha específica.
     */
    @Transactional(readOnly = true)
    public FlightStatsDTO getStatsForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Obtener conteos
        Long totalFlights = flightRepository.countByFechaDePartidaBetween(startOfDay, endOfDay);
        Long delayedFlights = flightRepository.countByEstadoAndFechaDePartidaBetween(
                FlightStatus.Retrasado,
                startOfDay,
                endOfDay
        );

        // Obtener probabilidad promedio
        Double avgProbability = flightRepository.averageProbabilidadByFechaDePartidaBetween(
                startOfDay,
                endOfDay
        );

        // Si no hay vuelos, avgProbability puede ser null
        if (avgProbability == null) {
            avgProbability = 0.0;
        }

        return new FlightStatsDTO(date, totalFlights, delayedFlights, avgProbability);
    }

    /**
     * Obtiene estadísticas de todos los vuelos (sin filtro de fecha).
     */
    @Transactional(readOnly = true)
    public FlightStatsDTO getAllTimeStats() {
        Long totalFlights = flightRepository.count();
        Long delayedFlights = flightRepository.countByEstadoAndFechaDePartidaBetween(
                FlightStatus.Retrasado,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2100, 12, 31, 23, 59)
        );

        Double avgProbability = flightRepository.averageProbabilidadByFechaDePartidaBetween(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2100, 12, 31, 23, 59)
        );

        if (avgProbability == null) {
            avgProbability = 0.0;
        }

        return new FlightStatsDTO(null, totalFlights, delayedFlights, avgProbability);
    }
}
