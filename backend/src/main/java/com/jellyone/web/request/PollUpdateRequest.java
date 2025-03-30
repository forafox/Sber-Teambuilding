package com.jellyone.web.request;

import com.jellyone.domain.enums.PollType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Represents a poll update request containing modified poll configuration and options.
 * Used for updating existing polls in chat conversations.
 */
@Schema(description = "Poll update request payload")
public record PollUpdateRequest(
        @Schema(
                description = "ID of the poll to update",
                example = "101",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Poll ID must not be null")
        @Positive(message = "Poll ID must be a positive number")
        Long id,

        @Schema(
                description = "Updated title/question of the poll",
                example = "Updated: Preferred meeting time options",
                minLength = 1,
                maxLength = 200,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Poll title must not be empty")
        @Size(min = 1, max = 200, message = "Poll title must be between 1 and 200 characters")
        String title,

        @Schema(
                description = "Updated type of the poll",
                implementation = PollType.class,
                allowableValues = {"SINGLE_CHOICE", "MULTIPLE_CHOICE", "OPEN_ENDED"},
                example = "MULTIPLE_CHOICE",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Poll type must be specified")
        PollType pollType,

        @ArraySchema(
                schema = @Schema(implementation = OptionUpdateRequest.class),
                arraySchema = @Schema(
                        description = "Updated list of poll options",
                        requiredMode = Schema.RequiredMode.REQUIRED
                )
        )
        @NotNull(message = "Poll options must not be null")
        List<@NotNull(message = "Poll option must not be null") OptionUpdateRequest> options
) {
}