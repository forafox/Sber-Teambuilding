package com.jellyone.controller;

import com.jellyone.service.EventService;
import com.jellyone.web.request.EventRequest;

import com.jellyone.web.response.EventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Event Management")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get all events")
    @GetMapping("/events")
    public Page<EventResponse> getEvents(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Principal principal
    ) {
        log.info("Received request to get all events");
        return eventService.getAll(principal.getName(), page, size).map(EventResponse::toResponse);
    }

    @Operation(summary = "Create event")
    @PostMapping("/events")
    public EventResponse createEvent(
            @Valid @RequestBody EventRequest request,
            Principal principal
    ) {
        log.info("Received request to create an event with title: {}", request.title());
        return EventResponse.toResponse(eventService.create(
                request.title(),
                principal.getName(),
                request.date(),
                request.participants()
        ));
    }

    @GetMapping("/events/{id}")
    public EventResponse getEventById(@PathVariable Long id) {
        log.info("Received request to get an event with id: {}", id);
        return EventResponse.toResponse(eventService.getById(id));
    }

    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
        log.info("Received request to delete an event with id: {}", id);
        eventService.delete(id);
    }

    @PutMapping("/events/{id}")
    public EventResponse updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest event
    ) {
        log.info("Received request to update an event with id: {}", id);
        return EventResponse.toResponse(eventService.update(
                id,
                event.title(),
                event.date(),
                event.participants()
        ));
    }

    @PatchMapping("/events/{id}/participants")
    public EventResponse updateEventParticipants(
            @PathVariable Long id,
            @Valid @RequestBody List<Long> participants
    ) {
        log.info("Received request to update an event with participants ids: {}", participants);
        return EventResponse.toResponse(eventService.updateParticipants(
                id,
                participants
        ));
    }
}
