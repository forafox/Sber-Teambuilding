package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.AuthService;
import com.jellyone.service.UserService;
import com.jellyone.web.request.SignInRequest;
import com.jellyone.web.request.SignUpRequest;
import com.jellyone.web.request.JwtRequest;
import com.jellyone.web.response.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication API",
        description = "Endpoints for user authentication, registration and token management")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "400",
                description = "Invalid input parameters or malformed request",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "401",
                description = "Unauthorized - Invalid credentials or expired token",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden - Insufficient permissions",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user",
            description = "Validates user credentials and returns JWT tokens for authorization",
            operationId = "loginUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public JwtResponse login(
            @Parameter(description = "User credentials", required = true)
            @Valid @RequestBody SignInRequest loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.username());
        return authService.login(new JwtRequest(loginRequest.username(), loginRequest.password()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Creates new user account and returns authentication tokens",
            operationId = "registerUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Registration successful",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "409",
                    description = "Conflict - User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public JwtResponse register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody SignUpRequest request) {
        userService.register(
                request.username(),
                request.password(),
                request.name(),
                request.email()
        );
        log.info("New user registration: {}", request.username());
        return authService.login(new JwtRequest(request.username(), request.password()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh authentication tokens",
            description = "Generates new access/refresh token pair using valid refresh token",
            operationId = "refreshTokens")
    @ApiResponse(responseCode = "200",
            description = "Tokens refreshed successfully",
            content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    public JwtResponse refresh(
            @Parameter(description = "Valid refresh token",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestBody @Valid @NotBlank String refreshToken) {
        log.debug("Token refresh request received");
        return authService.refresh(refreshToken);
    }
}