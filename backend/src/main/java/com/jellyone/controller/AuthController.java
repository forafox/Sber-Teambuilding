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
@Tag(name = "Authorization and Registration")
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = @Content(schema = @Schema(implementation = JwtResponse.class), mediaType = "application/json")
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class), mediaType = "application/json")
        )
})
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates user based on provided credentials and generates JWT token",
            operationId = "login"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class), mediaType = "application/json")
            )
    })
    public JwtResponse login(@Valid @RequestBody SignInRequest loginRequest) {
        log.info("Received login request with username: {}", loginRequest.username());
        return authService.login(new JwtRequest(loginRequest.username(), loginRequest.password()));
    }

    @PostMapping("/register")
    @Operation(
            summary = "User registration",
            description = "Registers a new user with provided details and generates JWT token",
            operationId = "register"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "409",
                    description = "",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class), mediaType = "application/json")
            )
    })
    public JwtResponse register(@RequestBody SignUpRequest request) {
        userService.register(
                request.username(),
                request.password(),
                request.name(),
                request.email()
        );
        log.info("Received registration request with username: {}", request.username());
        return authService.login(new JwtRequest(request.username(), request.password()));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh token",
            description = "Refreshes JWT token based on provided refresh token",
            operationId = "refresh"
    )
    public JwtResponse refresh(
            @RequestBody @Valid
            @NotBlank @Parameter(description = "Refresh token used to generate a new JWT token", required = true)
            String refreshToken
    ) {
        log.info("Received refresh request with refresh token: {}", refreshToken);
        return authService.refresh(refreshToken);
    }
}
