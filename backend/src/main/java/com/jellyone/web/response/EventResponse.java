package com.jellyone.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jellyone.domain.Event;
import com.jellyone.domain.enums.EventStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Represents an event response containing all event details.
 * Includes information about the event, participants, and associated chat.
 */
@Schema(description = "Complete event response with details and participants")
public record EventResponse(
        @Schema(
                description = "Unique identifier of the event",
                example = "123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Event ID must not be null")
        Long id,

        @Schema(
                description = "Title of the event",
                example = "Team Meeting",
                minLength = 3,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Event title must not be null")
        String title,

        @Schema(
                description = "Detailed description of the event",
                example = "Weekly team sync meeting",
                nullable = true
        )
        String description,

        @Schema(
                description = "Location of the event (physical or virtual)",
                example = "Conference Room A or Zoom",
                nullable = true
        )
        String location,

        @Schema(
                description = "Current status of the event",
                implementation = EventStatus.class,
                allowableValues = {"PLANNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"},
                example = "PLANNED",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Event status must not be null")
        EventStatus status,

        @Schema(
                description = "User who created the event",
                implementation = UserResponse.class,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Event author must not be null")
        UserResponse author,

        @Schema(
                description = "Date and time when the event starts",
                example = "2023-12-15T09:00:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm'Z'", timezone = "UTC")
        @NotNull(message = "Event date must not be null")
        LocalDateTime date,

        @ArraySchema(
                arraySchema = @Schema(
                        description = "List of participants in the event",
                        requiredMode = Schema.RequiredMode.REQUIRED
                ),
                schema = @Schema(implementation = UserResponse.class)
        )
        @NotNull(message = "Participants list must not be null")
        List<UserResponse> participants,

        @Schema(
                description = "ID of the chat associated with this event",
                example = "456",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Chat ID must not be null")
        Long chatId
) {

    /**
     * Converts an Event domain object to an EventResponse DTO.
     * Handles null author gracefully by returning null in the response.
     *
     * @param event the event entity to convert
     * @return fully populated EventResponse
     * @throws IllegalArgumentException if event is null
     */
    public static EventResponse toResponse(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStatus(),
                Optional.ofNullable(event.getAuthor())
                        .map(UserResponse::toResponse)
                        .orElse(null),
                event.getDate(),
                event.getParticipants().stream()
                        .map(UserResponse::toResponse)
                        .toList(),
                event.getChat().getId()
        );
    }
}