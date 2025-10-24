package com.github.gomestkd.eventmanagement.config;

import com.github.gomestkd.eventmanagement.services.InstanceInformationService;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private final InstanceInformationService instanceInformationService;

    public OpenApiConfig(InstanceInformationService instanceInformationService) {
        this.instanceInformationService = instanceInformationService;
        System.setProperty("springdoc.version", instanceInformationService.retrieveInstanceInfo());
    }

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(
                new Info()
                    .title("Event Management API " + instanceInformationService.retrieveInstanceInfo())
                    .version("v1")
                    .description("The event management API is responsible for managing events.")
                    .termsOfService("https://github.com/gomes-tkd?tab=repositories")
                    .license(
                        new License()
                            .name("Apache 2.0")
                            .url("https://github.com/gomes-tkd?tab=repositories")
                    )
                    .contact(
                        new Contact()
                            .name("Jose Gomes - Full Stack Developer")
                            .email("jgomestkd@gmail.com")
                    )
            );
    }
}
