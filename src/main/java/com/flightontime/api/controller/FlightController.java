package com.flightontime.api.controller;

import com.flightontime.api.dto.*;
import com.flightontime.api.model.Flight;
import com.flightontime.api.service.CsvService;
import com.flightontime.api.service.FlightService;
import com.flightontime.api.service.PredictionService;
import com.flightontime.api.service.StatsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
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
    private final StatsService statsService;

    public FlightController(
            FlightService flightService,
            PredictionService predictionService,
            CsvService csvService,
            StatsService statsService) {
        this.flightService = flightService;
        this.predictionService = predictionService;
        this.csvService = csvService;
        this.statsService = statsService;
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
     * Endpoint para obtener estadísticas de vuelos del día actual o fecha específica.
     * GET /flights/stats
     * GET /flights/stats?date=2027-03-15
     */
    @GetMapping("/stats")
    public ResponseEntity<FlightStatsDTO> getStats(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        FlightStatsDTO stats;

        if (date != null) {
            // Estadísticas para una fecha específica
            stats = statsService.getStatsForDate(date);
        } else {
            // Estadísticas del día actual
            stats = statsService.getStatsForToday();
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint para obtener estadísticas de todos los tiempos.
     * GET /flights/stats/all
     */
    @GetMapping("/stats/all")
    public ResponseEntity<FlightStatsDTO> getAllTimeStats() {
        FlightStatsDTO stats = statsService.getAllTimeStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint para obtener rankings de aerolíneas.
     * GET /flights/stats/airlines
     */
    @GetMapping("/stats/airlines")
    public ResponseEntity<AirlineRankingsDTO> getAirlineRankings() {
        AirlineRankingsDTO rankings = statsService.getAirlineRankings();
        return ResponseEntity.ok(rankings);
    }

    /**
     * Endpoint para obtener las últimas 5 predicciones realizadas.
     * GET /flights/stats/recent
     */
    @GetMapping("/stats/recent")
    public ResponseEntity<List<RecentPredictionDTO>> getRecentPredictions() {
        List<RecentPredictionDTO> recentPredictions = statsService.getRecentPredictions();
        return ResponseEntity.ok(recentPredictions);
    }

    /**
     * Endpoint para listar todos los vuelos con paginación.
     * GET /flights
     * GET /flights?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<Flight>> listar(
            @PageableDefault(size = 10, sort = {"aerolinea"}) Pageable paginacion) {

        Page<Flight> flights = flightService.listarTodo(paginacion);
        return ResponseEntity.ok(flights);
    }
}
