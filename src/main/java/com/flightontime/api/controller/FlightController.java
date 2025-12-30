package com.flightontime.api.controller;

import com.flightontime.api.dto.FlightData;
import com.flightontime.api.model.Flight;
import com.flightontime.api.repository.FlightRepository;
import com.flightontime.api.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    private FlightService service;


    @Transactional
    @PostMapping
    public void guardar(@RequestBody @Valid FlightData datos) {
        service.guardar(datos);
    }

    @GetMapping
    public Page<Flight> listar(@PageableDefault(size=10, sort={"aerolinea"}) Pageable paginacion) {
        return service.listarTodo(paginacion);
    }
}