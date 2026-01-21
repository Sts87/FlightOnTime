package com.flightontime.api.service;

import com.flightontime.api.dto.AirlineRankingsDTO;
import com.flightontime.api.dto.AirlineStatsDTO;
import com.flightontime.api.dto.FlightStatsDTO;
import com.flightontime.api.dto.RecentPredictionDTO;
import com.flightontime.api.model.Flight;
import com.flightontime.api.model.FlightStatus;
import com.flightontime.api.repository.FlightRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Obtiene rankings de aerolíneas (top 3 puntuales y top 3 retrasadas).
     */
    @Transactional(readOnly = true)
    public AirlineRankingsDTO getAirlineRankings() {
        List<String> airlines = flightRepository.findDistinctAirlines();
        List<AirlineStatsDTO> allStats = new ArrayList<>();

        // Calcular estadísticas para cada aerolínea
        for (String airline : airlines) {
            Long total = flightRepository.countByAerolinea(airline);
            Long delayed = flightRepository.countDelayedByAerolinea(airline);

            // Solo incluir aerolíneas con al menos 1 vuelo
            if (total > 0) {
                allStats.add(new AirlineStatsDTO(airline, total, delayed));
            }
        }

        // Ordenar por porcentaje de puntualidad (menor % de retraso = más puntual)
        List<AirlineStatsDTO> topPuntuales = allStats.stream()
                .sorted(Comparator.comparingDouble(AirlineStatsDTO::porcentajeRetrasados))
                .limit(3)
                .collect(Collectors.toList());

        // Ordenar por porcentaje de retraso (mayor % de retraso = más retrasada)
        List<AirlineStatsDTO> topRetrasadas = allStats.stream()
                .sorted(Comparator.comparingDouble(AirlineStatsDTO::porcentajeRetrasados).reversed())
                .limit(3)
                .collect(Collectors.toList());

        return new AirlineRankingsDTO(topPuntuales, topRetrasadas);
    }

    /**
     * Obtiene las últimas 5 predicciones realizadas.
     */
    @Transactional(readOnly = true)
    public List<RecentPredictionDTO> getRecentPredictions() {
        List<Flight> recentFlights = flightRepository.findRecentPredictions(PageRequest.of(0, 5));

        return recentFlights.stream()
                .map(RecentPredictionDTO::new)
                .collect(Collectors.toList());
    }
}
