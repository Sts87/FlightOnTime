package com.flightontime.api.service;

import com.flightontime.api.dto.FlightData;
import com.flightontime.api.model.Flight;
import com.flightontime.api.repository.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de dominio para gestionar entidades Flight.
 * Responsable de operaciones CRUD y consultas sobre vuelos.
 */
@Service
public class FlightService {

    private final FlightRepository repository;

    public FlightService(FlightRepository repository) {
        this.repository = repository;
    }

    /**
     * Guarda un vuelo en la base de datos.
     *
     * @deprecated Usar PredictionService.predict() que incluye la lógica de predicción
     */
    @Deprecated
    @Transactional
    public Flight guardar(FlightData datos) {
        // Este método se mantiene por compatibilidad pero se recomienda
        // usar PredictionService para nueva funcionalidad
        throw new UnsupportedOperationException(
                "Usar PredictionService.predict() en lugar de este método"
        );
    }

    /**
     * Lista todos los vuelos con paginación.
     */
    @Transactional(readOnly = true)
    public Page<Flight> listarTodo(Pageable paginacion) {
        return repository.findAll(paginacion);
    }

    /**
     * Guarda un vuelo (uso interno para otros servicios).
     */
    @Transactional
    public Flight save(Flight flight) {
        return repository.save(flight);
    }
}
