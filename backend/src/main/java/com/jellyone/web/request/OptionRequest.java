package com.jellyone.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a poll option request containing the option title.
 * Used for creating or updating poll options in messaging system.
 */
@Schema(description = "Poll option request payload")
public record OptionRequest(
        @Schema(
                description = "Title/text of the poll option",
                example = "Agree",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Option title must not be empty")
        String title
) {
}