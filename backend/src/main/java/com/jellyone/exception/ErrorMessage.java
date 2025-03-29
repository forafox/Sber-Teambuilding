package com.jellyone.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Schema(description = "Error message model")
public record ErrorMessage(
        @NotNull @Schema(example = "404") int statusCode,
        @NotNull @Schema(example = "2024-04-11T12:00:00.000Z") Date timestamp,
        @NotNull @Schema(example = "Resource not found") String description,
        @NotNull @Schema(example = "The requested resource could not be found") String message
) {}