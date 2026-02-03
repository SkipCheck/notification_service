package com.aston.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Notification Service API",
                version = "1.0",
                description = "API для управления уведомлениями и отправки email",
                contact = @Contact(
                        name = "Поддержка",
                        email = "support@aston.example.com",
                        url = "https://aston.example.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        description = "Локальный сервер",
                        url = "http://localhost:8081"
                )
        }
)
public class OpenApiConfig {
}