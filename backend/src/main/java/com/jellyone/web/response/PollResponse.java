package com.jellyone.web.response;

import com.jellyone.domain.Poll;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Represents a poll response containing poll configuration and voting options.
 * Includes the poll question, type, and available voting options with their current votes.
 */
@Schema(description = "Poll response with configuration and voting options")
public record PollResponse(
        @Schema(
                description = "Unique identifier of the poll",
                example = "101",
                minimum = "1",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Poll ID must not be null")
        @Positive(message = "Poll ID must be a positive number")
        Long id,

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
                description = "Type of the poll determining voting behavior",
                allowableValues = {"SINGLE_CHOICE", "MULTIPLE_CHOICE", "OPEN_ENDED"},
                example = "SINGLE_CHOICE",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Poll type must be specified")
        String pollType,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "Available voting options for this poll",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(implementation = OptionResponse.class)
        )
        @NotNull(message = "Poll options must not be null")
        List<OptionResponse> options
) {

    /**
     * Converts a Poll domain object to a PollResponse DTO.
     *
     * @param poll the poll entity to convert
     * @return fully populated PollResponse
     * @throws IllegalArgumentException if poll is null
     */
    public static PollResponse toResponse(Poll poll) {
        if (poll == null) {
            throw new IllegalArgumentException("Poll cannot be null");
        }

        return new PollResponse(
                poll.getId(),
                poll.getTitle(),
                poll.getPollType().name(),
                poll.getOptions().stream()
                        .map(OptionResponse::toResponse)
                        .toList()
        );
    }
}