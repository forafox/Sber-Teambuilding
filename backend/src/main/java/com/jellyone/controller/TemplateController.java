package com.jellyone.controller;

import com.jellyone.domain.Event;
import com.jellyone.domain.Task;
import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.TemplateService;
import com.jellyone.web.request.EventRequest;
import com.jellyone.web.response.EventResponse;
import com.jellyone.web.response.TaskResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequiredArgsConstructor
@Tag(name = "Template Management API",
        description = "Endpoints for managing and applying event and task templates")
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
                description = "Template not found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/events")
    @Operation(summary = "Get all event templates",
            description = "Retrieves a list of all available event templates",
            operationId = "getAllEventTemplates")
    @ApiResponse(responseCode = "200",
            description = "Event templates retrieved successfully",
            content = @Content(schema = @Schema(implementation = EventResponse.class)))
    public List<EventResponse> getAllTemplatesEvent() {
        log.info("Received request to get all event templates");
        return templateService.getAllTemplatesEvent().stream().map(EventResponse::toResponse).toList();
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Get event template by ID",
            description = "Retrieves details of a specific event template",
            operationId = "getEventTemplateById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Event template retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Event template not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public EventResponse getTemplateEventById(
            @Parameter(description = "ID of the event template", required = true, example = "1")
            @PathVariable Long id) {
        log.info("Received request to get event template with id: {}", id);
        return EventResponse.toResponse(templateService.getTemplateEventById(id));
    }

    @GetMapping("/events/{eventId}/tasks")
    @Operation(summary = "Get all task templates for event",
            description = "Retrieves all task templates associated with an event template",
            operationId = "getTaskTemplatesByEvent")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Task templates retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Event template not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public List<TaskResponse> getAllTasksByEventId(
            @Parameter(description = "ID of the event template", required = true, example = "1")
            @PathVariable Long eventId) {
        log.info("Received request to get task templates for event template {}", eventId);
        return templateService.getAllTemplateTasksByEventId(eventId).stream().map(TaskResponse::toResponse).toList();
    }

    @GetMapping("/events/{eventId}/tasks/{id}")
    @Operation(summary = "Get task template by ID",
            description = "Retrieves details of a specific task template within an event template",
            operationId = "getTaskTemplateById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Task template retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Task template not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public TaskResponse getTemplateTaskById(
            @Parameter(description = "ID of the event template", required = true, example = "1")
            @PathVariable Long eventId,

            @Parameter(description = "ID of the task template", required = true, example = "1")
            @PathVariable Long id) {
        log.info("Received request to get task template with id: {} for event template {}", id, eventId);
        return TaskResponse.toResponse(templateService.getTemplateTaskById(eventId, id));
    }

    @PostMapping("/events/{eventId}")
    @Operation(summary = "Apply template",
            description = """
                    Applies an event template to create a new event with all associated tasks.
                    """,
            operationId = "applyTemplate")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Template applied successfully",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Template not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public EventResponse applyTemplate(
            @Parameter(description = "ID of the template to apply", required = true, example = "1")
            @PathVariable Long eventId,

            Principal principal) {
        log.info("Received request to apply template {}", eventId);
        return EventResponse.toResponse(templateService.applyTemplate(eventId, principal.getName()));
    }
}