package com.flightontime.api.service;

import com.flightontime.api.dto.FlightData;
import com.flightontime.api.model.Flight;
import com.flightontime.api.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository repository;

    @Transactional
    public void guardar (FlightData datos) {
        repository.save(new Flight(datos));
    }

    @Transactional(readOnly = true)
    public Page<Flight> listarTodo(Pageable paginacion) {
        return repository.findAll(paginacion);
    }
}
