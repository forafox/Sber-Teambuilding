package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents user registration data.
 * Used for creating new user accounts in the system.
 */
@Schema(description = "User registration request payload")
public record SignUpRequest(
        @Schema(
                description = "Unique username for the new account",
                example = "john_doe",
                minLength = 4,
                maxLength = 32,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Username must not be blank")
        @Size(min = 4, max = 32, message = "Username must be 4-32 characters")
        String username,

        @Schema(
                description = "Password for the new account",
                example = "P@ssw0rd123!",
                minLength = 5,
                maxLength = 64,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Password must not be blank")
        @Size(min = 5, max = 64, message = "Password must be 8-64 characters")
        String password,

        @Schema(
                description = "Full name of the user",
                example = "John Doe",
                minLength = 2,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Name must not be blank")
        @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
        String name,

        @Schema(
                description = "Email address of the user",
                example = "john.doe@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email should be valid")
        String email
) {
}