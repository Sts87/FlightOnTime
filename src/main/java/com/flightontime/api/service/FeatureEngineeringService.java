package com.flightontime.api.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightontime.api.dto.FlightData;
import com.flightontime.api.exception.FeatureEngineeringException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Servicio responsable de transformar FlightData en features
 * que el modelo ONNX puede procesar.
 */
@Service
public class FeatureEngineeringService {

    @Value("classpath:airport_mappings.json")
    private Resource mappingsResource;

    private Map<String, Double> fromMap;
    private Map<String, Double> toMap;
    private double globalMean;

    // Mapeos para aerolíneas (OneHotEncoding)
    private static final List<String> AIRLINES = Arrays.asList(
            "??", "US", "AA", "DL", "OO", "EV", "XE", "UA", "MQ", "WN"
    );

    // Días de la semana
    private static final List<String> DAYS_OF_WEEK = Arrays.asList(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> mappings = mapper.readValue(
                    mappingsResource.getInputStream(),
                    Map.class
            );

            this.fromMap = (Map<String, Double>) mappings.get("from_map");
            this.toMap = (Map<String, Double>) mappings.get("to_map");
            this.globalMean = (Double) mappings.get("global_mean");

        } catch (IOException e) {
            throw new FeatureEngineeringException(
                    "Error al cargar los mapeos de aeropuertos: " + e.getMessage(), e
            );
        }
    }

    /**
     * Convierte FlightData en tensores ONNX listos para el modelo.
     */
    public Map<String, OnnxTensor> prepareFeatures(FlightData flight, OrtEnvironment env) {
        try {
            Map<String, OnnxTensor> features = new HashMap<>();

            // 1. AirportFrom (Target Encoding) - FLOAT
            float airportFrom = fromMap.getOrDefault(
                    flight.origen().toUpperCase(),
                    globalMean
            ).floatValue();
            features.put("AirportFrom", createFloatTensor(env, new float[][]{{airportFrom}}));

            // 2. AirportTo (Target Encoding) - FLOAT
            float airportTo = toMap.getOrDefault(
                    flight.destino().toUpperCase(),
                    globalMean
            ).floatValue();
            features.put("AirportTo", createFloatTensor(env, new float[][]{{airportTo}}));

            // 3. Length (distancia) - FLOAT
            float length = flight.distancia().floatValue();
            features.put("Length", createFloatTensor(env, new float[][]{{length}}));

            // 4. Hour - FLOAT
            float hour = (float) flight.fechaDePartida().getHour();
            features.put("Hour", createFloatTensor(env, new float[][]{{hour}}));

            // 5. Airline - STRING (código de aerolínea directo)
            String airline = flight.aerolinea().toUpperCase();
            if (!AIRLINES.contains(airline)) {
                System.out.println("⚠️ Aerolínea no reconocida: " + airline + ", usando AA por defecto");
                airline = "??"; // Default a AA si no existe
            }
            features.put("Airline", createStringTensor(env, new String[][]{{airline}}));

            // 6. DayOfWeek - INT64 (0=Monday, 1=Tuesday, ..., 6=Sunday)
            // Java DayOfWeek: MONDAY=1, TUESDAY=2, ..., SUNDAY=7
            // Convertimos a: Monday=0, Tuesday=1, ..., Sunday=6
            long dayOfWeek = flight.fechaDePartida().getDayOfWeek().getValue() - 1;
            features.put("DayOfWeek", createLongTensor(env, new long[][]{{dayOfWeek}}));

            return features;

        } catch (OrtException e) {
            throw new FeatureEngineeringException(
                    "Error al crear tensores ONNX: " + e.getMessage(), e
            );
        }
    }

    private OnnxTensor createFloatTensor(OrtEnvironment env, float[][] data) throws OrtException {
        return OnnxTensor.createTensor(env, data);
    }

    private OnnxTensor createLongTensor(OrtEnvironment env, long[][] data) throws OrtException {
        return OnnxTensor.createTensor(env, data);
    }

    private OnnxTensor createStringTensor(OrtEnvironment env, String[][] data) throws OrtException {
        return OnnxTensor.createTensor(env, data);
    }

    /**
     * Cierra y limpia los tensores después de su uso.
     */
    public void cleanupTensors(Map<String, OnnxTensor> tensors) {
        tensors.values().forEach(OnnxTensor::close);
    }
}