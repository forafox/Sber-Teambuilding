package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.EventService;
import com.jellyone.service.TokenService;
import com.jellyone.service.UserService;
import com.jellyone.web.response.EventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
@Tag(name = "Token Management",
        description = "Endpoints for generating and verifying event participation tokens")
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
                description = "Resource not found (event or user)",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "422",
                description = "Invalid or expired token",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class TokenController {
    private final TokenService tokenService;
    private final EventService eventService;
    private final UserService userService;

    @PostMapping("/events/{eventId}")
    @Operation(summary = "Generate event participation token",
            description = """
                    Creates a secure token that can be used to join a specific event.
                    """,
            operationId = "createEventToken")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Token generated successfully",
                    content = @Content(schema = @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))),
            @ApiResponse(responseCode = "404",
                    description = "Event not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public String createEventToken(
            @Parameter(description = "ID of the event to generate token for",
                    required = true,
                    example = "123")
            @PathVariable Long eventId) {
        log.info("Generating participation token for event ID: {}", eventId);
        return tokenService.encryptEventId(eventId);
    }

    @PostMapping(path = "/verify", consumes = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Verify and use participation token",
            description = """
                    Validates an event participation token and adds the current user to the event.
                    """,
            operationId = "verifyEventToken")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Token verified and user added to event",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Missing or malformed token",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "422",
                    description = "Invalid or expired token",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public EventResponse verifyEventToken(
            @Parameter(description = "Participation token to verify",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestBody @NotNull String token,
            Principal principal) {
        log.info("Verifying participation token for user: {}", principal.getName());
        return EventResponse.toResponse(eventService.updateParticipants(
                tokenService.decryptEventId(token),
                List.of(userService.getByUsername(principal.getName()).getId())
        ));
    }
}