package com.flightontime.api.service;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.flightontime.api.dto.FlightData;
import com.flightontime.api.model.Flight;
import com.flightontime.api.model.FlightStatus;
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
    public Flight guardar(FlightData datos) {

        double probabilidad = calcularProbabilidad(datos);
        FlightStatus estado = calcularEstado(probabilidad);

        Flight vuelo = new Flight(datos, estado, probabilidad);
        return repository.save(vuelo);
    }

    @Transactional(readOnly = true)
    public Page<Flight> listarTodo(Pageable paginacion) {
        return repository.findAll(paginacion);
    }

    private double calcularProbabilidad(FlightData datos) {
        double probabilidad = 0.1;

        if (datos.distancia() > 2000) {
            probabilidad += 0.2;
        }

        int hora = datos.fechaDePartida().getHour();
        if (hora >= 18 && hora <= 22) {
            probabilidad += 0.15;
        }

        return Math.min(probabilidad, 1.0);
    }

    private FlightStatus calcularEstado(double probabilidad) {
        return probabilidad >= 0.5
                ? FlightStatus.DELAYED
                : FlightStatus.ON_TIME;
    }
}
