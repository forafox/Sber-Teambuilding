package com.jellyone.web.request;

import com.jellyone.domain.enums.PollType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Represents a poll creation request containing poll configuration and options.
 * Used for creating new polls in chat conversations.
 */
@Schema(description = "Poll creation request payload")
public record PollRequest(
        @Schema(
                description = "Title/question of the poll",
                example = "What's your preferred meeting time?",
                minLength = 1,
                maxLength = 200,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Poll title must not be empty")
        @Size(min = 1, max = 200, message = "Poll title must be between 1 and 200 characters")
        String title,

        @Schema(
                description = "Type of the poll",
                implementation = PollType.class,
                allowableValues = {"SINGLE", "MULTIPLE"},
                example = "SINGLE",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Poll type must be specified")
        PollType pollType,

        @ArraySchema(
                schema = @Schema(implementation = OptionRequest.class),
                arraySchema = @Schema(
                        description = "List of poll options",
                        requiredMode = Schema.RequiredMode.REQUIRED
                )
        )
        @NotNull(message = "Poll options must not be null")
        List<@NotNull(message = "Poll option must not be null") OptionRequest> options
) {
}