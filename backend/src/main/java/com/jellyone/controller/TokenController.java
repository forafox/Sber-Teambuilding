package com.jellyone.controller;

import com.jellyone.service.EventService;
import com.jellyone.service.TokenService;
import com.jellyone.service.UserService;
import com.jellyone.web.response.EventResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Event Token Management")
@SecurityRequirement(name = "JWT")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final EventService eventService;
    private final UserService userService;

    @Operation(summary = "Create event token")
    @PostMapping("/events/{eventId}")
    public String createEventToken(
            @PathVariable Long eventId
    ) {
        log.info("Received request to create event token");
        return tokenService.encryptEventId(eventId);
    }

    @Operation(summary = "Verify event token")
    @PostMapping("/verify")
    public EventResponse verifyEventToken(
            @RequestBody @NotNull String token,
            Principal principal
    ) {
        log.info("Received request to verify event token");
        return EventResponse.toResponse(eventService.updateParticipants(
                tokenService.decryptEventId(token),
                List.of(userService.getByUsername(principal.getName()).getId())
        ));
    }
}
