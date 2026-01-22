package com.flightontime.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;

/**
 * Configuración del modelo de Machine Learning.
 * Carga parámetros como el threshold óptimo para clasificación.
 */
@Configuration
public class ModelConfiguration {

    @Value("classpath:flight_delay_threshold.json")
    private Resource thresholdResource;

    private double bestThreshold;

    @PostConstruct
    public void init() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> config = mapper.readValue(
                    thresholdResource.getInputStream(),
                    Map.class
            );

            this.bestThreshold = (Double) config.get("best_threshold");

            System.out.println("✅ Threshold óptimo cargado: " + this.bestThreshold);

        } catch (IOException e) {
            this.bestThreshold = 0.5; // Valor por defecto
        }
    }

    public double getBestThreshold() {
        return bestThreshold;
    }
}
