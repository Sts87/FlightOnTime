package com.flightontime.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 */
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI flightOnTimeAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de desarrollo");

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
                .description("API REST para predecir retrasos de vuelos utilizando Machine Learning con ONNX. " +
                        "Permite realizar predicciones individuales, por lote mediante CSV, y consultar estadísticas.")
                .termsOfService("https://flightontime.dev/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
