package com.flightontime.api.service;

import ai.onnxruntime.OnnxTensor;
import com.flightontime.api.config.ModelConfiguration;
import com.flightontime.api.dto.BatchPredictionResultDTO;
import com.flightontime.api.dto.FlightData;
import com.flightontime.api.dto.PredictionResponse;
import com.flightontime.api.model.Flight;
import com.flightontime.api.model.FlightStatus;
import com.flightontime.api.repository.FlightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Servicio que coordina las predicciones de vuelos.
 * Orquesta el feature engineering, la predicción del modelo y el guardado.
 */
@Service
public class PredictionService {

    private final OnnxModelService modelService;
    private final FeatureEngineeringService featureService;
    private final FlightRepository repository;
    private final ModelConfiguration modelConfig;

    public PredictionService(
            OnnxModelService modelService,
            FeatureEngineeringService featureService,
            FlightRepository repository,
            ModelConfiguration modelConfig) {
        this.modelService = modelService;
        this.featureService = featureService;
        this.repository = repository;
        this.modelConfig = modelConfig;
    }

    /**
     * Realiza una predicción individual y guarda el resultado.
     */
    @Transactional
    public PredictionResponse predict(FlightData flightData) {
        Map<String, OnnxTensor> features = null;

        try {
            // 1. Preparar features
            features = featureService.prepareFeatures(
                    flightData,
                    modelService.getEnvironment()
            );

            // 2. Ejecutar predicción
            Map<String, Object> result = modelService.predict(features);
            double probability = (Double) result.get("probability");

            // 3. Convertir a FlightStatus usando el threshold óptimo
            double threshold = modelConfig.getBestThreshold();
            FlightStatus status = probability >= threshold
                    ? FlightStatus.Retrasado
                    : FlightStatus.Puntual;

            // 4. Redondear probabilidad
            double roundedProbability = Math.round(probability * 100.0) / 100.0;

            // 5. Guardar en base de datos
            Flight flight = new Flight(flightData, status, probability);
            repository.save(flight);

            return new PredictionResponse(status, roundedProbability);

        } catch (Exception e) {
            throw e;
        } finally {
            // 6. Limpiar recursos
            if (features != null) {
                featureService.cleanupTensors(features);
            }
        }
    }

    /**
     * Procesa predicciones en lote.
     */
    @Transactional
    public List<BatchPredictionResultDTO> processBatchPrediction(List<FlightData> flightDataList) {
        return flightDataList.stream()
                .map(this::predictAndMap)
                .toList();
    }

    /**
     * Predice y mapea a DTO de resultado en batch.
     */
    private BatchPredictionResultDTO predictAndMap(FlightData flightData) {
        Map<String, OnnxTensor> features = null;

        try {
            // 1. Preparar features
            features = featureService.prepareFeatures(
                    flightData,
                    modelService.getEnvironment()
            );

            // 2. Ejecutar predicción
            Map<String, Object> result = modelService.predict(features);
            double probability = (Double) result.get("probability");

            // 3. Convertir a FlightStatus usando el threshold óptimo
            double threshold = modelConfig.getBestThreshold();
            FlightStatus status = probability >= threshold
                    ? FlightStatus.Retrasado
                    : FlightStatus.Puntual;

            // 4. Guardar en base de datos
            Flight flight = new Flight(flightData, status, probability);
            repository.save(flight);

            // 5. Mapear a DTO de respuesta
            return new BatchPredictionResultDTO(
                    flight.getAerolinea(),
                    flight.getOrigen(),
                    flight.getDestino(),
                    flight.getFechaDePartida(),
                    flight.getDistancia(),
                    status.name(),
                    Math.round(probability * 100.0) / 100.0
            );

        } finally {
            // 6. Limpiar recursos
            if (features != null) {
                featureService.cleanupTensors(features);
            }
        }
    }
}
