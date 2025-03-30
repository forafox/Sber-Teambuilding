package com.jellyone.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "PROSTO SBER HACK API",
                description = "Sample API",
                version = "1.0.0"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Default Server URL")
        },
        tags = {
                @Tag(name = "Authorization and Registration", description = "API for users"),
                @Tag(name = "User Management", description = "API for users"),
                @Tag(name = "Event Management", description = "API for events"),
                @Tag(name = "Notification Management", description = "API for notifications"),
                @Tag(name = "Task Management", description = "API for tasks"),
                @Tag(name = "Message Management", description = "API for messages"),
                @Tag(name = "Chat Management", description = "API for chats"),
                @Tag(name = "Token Management", description = "API for event tokens"),
                @Tag(name = "Template Management", description = "API for templates"),
                @Tag(name = "Email Management", description = "API for sending email reports"),
                @Tag(name = "Telegram Management", description = "API for managing Telegram users"),
        }
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
}
