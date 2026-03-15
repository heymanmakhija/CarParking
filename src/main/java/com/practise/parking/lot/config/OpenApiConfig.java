package com.practise.parking.lot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI parkingLotOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Parking Lot API")
                        .description("API documentation for parking lot management endpoints.")
                        .version("v1")
                        .contact(new Contact().name("Parking Lot Service"))
                        .license(new License().name("Internal Use")));
    }
}
