package com.jellyone.controller;

import com.jellyone.adapters.mail.SenderService;
import com.jellyone.adapters.telegram.TelegramNotificationService;
import com.jellyone.domain.enums.EventStatus;
import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.EventService;
import com.jellyone.web.request.EventRequest;
import com.jellyone.web.response.EventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Event Management API",
        description = "Endpoints for creating, managing and querying events")
@SecurityRequirement(name = "JWT")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "400",
                description = "Invalid input parameters or malformed request",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "401",
                description = "Unauthorized - Invalid or expired JWT token",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden - Insufficient permissions",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "404",
                description = "Event not found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class EventController {

    private final EventService eventService;
    private final TelegramNotificationService telegramNotificationService;
    private final SenderService senderService;

    @GetMapping("/events")
    @Operation(summary = "Get paginated list of events",
            description = "Retrieves events with optional status filtering",
            operationId = "getEvents")
    @ApiResponse(responseCode = "200",
            description = "Events retrieved successfully",
            content = @Content(schema = @Schema(implementation = EventResponse.class)))
    public Page<EventResponse> getEvents(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Status filter for events", example = "ACTIVE")
            @RequestParam(required = false) EventStatus status,

            Principal principal) {
        log.info("Received request to get all events");
        return eventService.getAll(principal.getName(), status, page, size).map(EventResponse::toResponse);
    }

    @PostMapping("/events")
    @Operation(summary = "Create new event",
            description = "Creates an event and notifies participants via Telegram",
            operationId = "createEvent")
    @ApiResponse(responseCode = "200",
            description = "Event created successfully",
            content = @Content(schema = @Schema(implementation = EventResponse.class)))
    public EventResponse createEvent(
            @Parameter(description = "Event creation data", required = true)
            @Valid @RequestBody EventRequest request,
            Principal principal) {
        log.info("Received request to create an event with title: {}", request.title());
        request.participants().forEach(participantId ->
                telegramNotificationService.sendNewEventNotification(participantId, request.title()));
        return EventResponse.toResponse(eventService.create(
                request.title(),
                request.description(),
                request.location(),
                request.status(),
                principal.getName(),
                request.date(),
                request.participants()
        ));
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Get event by ID",
            description = "Retrieves complete details for a specific event",
            operationId = "getEventById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Event retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Event not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public EventResponse getEventById(
            @Parameter(description = "ID of the event to retrieve",
                    required = true,
                    example = "123")
            @PathVariable Long id) {
        log.info("Received request to get an event with id: {}", id);
        return EventResponse.toResponse(eventService.getById(id));
    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "Delete event",
            description = "Permanently removes an event",
            operationId = "deleteEvent")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Event not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public void deleteEvent(
            @Parameter(description = "ID of the event to delete",
                    required = true,
                    example = "123")
            @PathVariable Long id) {
        log.info("Received request to delete an event with id: {}", id);
        eventService.delete(id);
    }

    @PutMapping("/events/{id}")
    @Operation(summary = "Update event",
            description = "Updates all fields of an existing event",
            operationId = "updateEvent")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Event updated successfully",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Event not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public EventResponse updateEvent(
            @Parameter(description = "ID of the event to update",
                    required = true,
                    example = "123")
            @PathVariable Long id,

            @Parameter(description = "Updated event data", required = true)
            @Valid @RequestBody EventRequest event) {
        log.info("Received request to update an event with id: {}", id);
        handleEventStatusChange(event.status(), event.participants(), event.title(), id);
        return EventResponse.toResponse(eventService.update(
                id,
                event.title(),
                event.description(),
                event.location(),
                event.status(),
                event.date(),
                event.participants()
        ));
    }

    @PatchMapping("/events/{id}/participants")
    @Operation(summary = "Update event participants",
            description = "Modifies the participant list for an event",
            operationId = "updateEventParticipants")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Participants updated successfully",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Event not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public EventResponse updateEventParticipants(
            @Parameter(description = "ID of the event to update",
                    required = true,
                    example = "123")
            @PathVariable Long id,

            @Parameter(description = "List of participant IDs",
                    required = true,
                    example = "[1, 2, 3]")
            @Valid @RequestBody List<Long> participants) {
        log.info("Received request to update an event with participants ids: {}", participants);
        return EventResponse.toResponse(eventService.updateParticipants(id, participants));
    }

    @PostMapping("/events/prompt")
    @Operation(summary = "Create event from prompt",
            description = "Generates an event based on natural language description",
            operationId = "createEventFromPrompt")
    @ApiResponse(responseCode = "200",
            description = "Event created successfully from prompt",
            content = @Content(schema = @Schema(implementation = EventResponse.class)))
    public EventResponse createFromPrompt(
            @Parameter(description = "Natural language description of the event",
                    required = true,
                    example = "Выезд на природу, шашлыки и активные игры")
            @Valid @RequestBody String prompt,

            Principal principal) {
        return EventResponse.toResponse(eventService.createFromPrompt(principal.getName(), prompt));
    }


    private void handleEventStatusChange(EventStatus status, List<Long> participants, String title, Long id) {
        if (status == EventStatus.DONE) {
            participants.forEach(participantId -> {
                senderService.sendMail(participantId, id);
                telegramNotificationService.sendEventEndNotification(participantId, title);
            });
        }
    }
}