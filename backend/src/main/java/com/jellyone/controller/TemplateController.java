package com.jellyone.controller;

import com.jellyone.domain.Event;
import com.jellyone.domain.Task;
import com.jellyone.service.TemplateService;
import com.jellyone.web.request.EventRequest;
import com.jellyone.web.response.EventResponse;
import com.jellyone.web.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/templates")
@Tag(name = "Template Management")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class TemplateController {

    private final TemplateService templateService;

    @Operation(summary = "Get all templates")
    @GetMapping("/events")
    public List<EventResponse> getAllTemplatesEvent() {
        log.info("Received request to get all templates");
        return templateService.getAllTemplatesEvent().stream().map(EventResponse::toResponse).toList();
    }

    @Operation(summary = "Get template event by id")
    @GetMapping("/events/{id}")
    public EventResponse getTemplateEventById(
            @PathVariable Long id
    ) {
        log.info("Received request to get template event with id: {}", id);
        return EventResponse.toResponse(templateService.getTemplateEventById(id));
    }

    @Operation(summary = "Get all templates tasks by event id")
    @GetMapping("/events/{eventId}/tasks")
    public List<TaskResponse> getAllTasksByEventId(
            @PathVariable Long eventId
    ) {
        log.info("Received request to get all tasks by event id {}", eventId);
        return templateService.getAllTemplateTasksByEventId(eventId).stream().map(TaskResponse::toResponse).toList();
    }

    @Operation(summary = "Get templatetask by id")
    @GetMapping("/events/{eventId}/tasks/{id}")
    public TaskResponse getTemplateTaskById(
            @PathVariable Long eventId,
            @PathVariable Long id
    ) {
        log.info("Received request to get template task with id: {}", id);
        return TaskResponse.toResponse(templateService.getTemplateTaskById(eventId, id));
    }

    @Operation(summary = "Apply template")
    @PostMapping("/events/{eventId}")
    public EventResponse applyTemplate(
            @PathVariable Long eventId,
            Principal principal
    ) {
        log.info("Received request to apply template {}", eventId);
        return EventResponse.toResponse(templateService.applyTemplate(eventId, principal.getName()));
    }
}
