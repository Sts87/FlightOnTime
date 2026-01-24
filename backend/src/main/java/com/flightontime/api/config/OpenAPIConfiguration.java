package com.flightontime.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Value("${flightontime.openapi.dev-url:http://localhost:8080}")
    private String devUrl;

    @Value("${flightontime.openapi.prod-url:https://fly-on-time-production.up.railway.app}")
    private String prodUrl;

    @Bean
    public OpenAPI flightOnTimeAPI() {
        // Servidor Local
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Servidor de desarrollo (Local)");

        // Servidor Producción
        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Servidor de producción (Railway)");

        Contact contact = new Contact();
        contact.setEmail("contact@flightontime.dev");
        contact.setName("FlightOnTime Team");
        contact.setUrl("https://flightontime.dev");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("FlightOnTime API - Predicción de Retrasos de Vuelos")
                .version("1.0.0")
                .contact(contact)
                .description("API REST para predecir retrasos de vuelos utilizando Machine Learning con ONNX.")
                .termsOfService("https://flightontime.dev/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}