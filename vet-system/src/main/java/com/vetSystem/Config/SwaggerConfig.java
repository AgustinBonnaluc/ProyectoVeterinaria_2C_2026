package com.vetSystem.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI clinicaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Clínica Veterinaria 'Patitas Felices'")
                        .description("API REST del monolito: gestión de dueños, mascotas, veterinarios y turnos.")
                        .version("1.0.0"));
    }
}
