package com.flightontime.api.controller;

import com.flightontime.api.dto.BatchPredictionResultDTO;
import com.flightontime.api.dto.FlightData;
import com.flightontime.api.dto.PredictionResponse;
import com.flightontime.api.model.Flight;
import com.flightontime.api.service.CsvService;
import com.flightontime.api.service.FlightService;
import com.flightontime.api.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador REST para gestionar vuelos y predicciones.
 */
@RestController
@RequestMapping("/flights")
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET})
public class FlightController {

    private final FlightService flightService;
    private final PredictionService predictionService;
    private final CsvService csvService;

    public FlightController(
            FlightService flightService,
            PredictionService predictionService,
            CsvService csvService) {
        this.flightService = flightService;
        this.predictionService = predictionService;
        this.csvService = csvService;
    }

    /**
     * Endpoint para realizar una predicción individual.
     */
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predict(
            @RequestBody @Valid FlightData datos) {

        PredictionResponse response = predictionService.predict(datos);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para predicciones en lote mediante archivo CSV.
     */
    @PostMapping("/batch/predict")
    public ResponseEntity<List<BatchPredictionResultDTO>> predecirBatch(
            @RequestParam("file") MultipartFile file) {

        // 1. Parsear CSV
        List<FlightData> vuelosParaPredecir = csvService.parse(file);

        // 2. Procesar predicciones en lote
        List<BatchPredictionResultDTO> resultados =
                predictionService.processBatchPrediction(vuelosParaPredecir);

        // 3. Retornar resultados
        return ResponseEntity.ok(resultados);
    }

    /**
     * Endpoint para listar todos los vuelos con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<Flight>> listar(
            @PageableDefault(size = 10, sort = {"aerolinea"}) Pageable paginacion) {

        Page<Flight> flights = flightService.listarTodo(paginacion);
        return ResponseEntity.ok(flights);
    }
}
