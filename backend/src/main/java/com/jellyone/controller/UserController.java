package com.jellyone.controller;

import com.jellyone.exception.ErrorMessage;
import com.jellyone.service.UserService;
import com.jellyone.web.response.UserResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User Management API",
        description = "Endpoints for retrieving and managing user information")
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
                description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "500",
                description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
})
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user",
            description = "Retrieves detailed information about the currently authenticated user",
            operationId = "getCurrentUser")
    @ApiResponse(responseCode = "200",
            description = "Current user details retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserResponse.class)))
    public UserResponse me(Principal principal) {
        log.info("Retrieving current user details for: {}", principal.getName());
        return UserResponse.toResponse(userService.getByUsername(principal.getName()));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID",
            description = "Retrieves user details by their unique identifier",
            operationId = "getUserById")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "User details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessage.class)))
    })
    public UserResponse getUserById(
            @Parameter(description = "ID of the user to retrieve",
                    required = true,
                    example = "123")
            @PathVariable Long id) {
        log.info("Retrieving user with ID: {}", id);
        return UserResponse.toResponse(userService.getById(id));
    }

    @GetMapping("/users")
    @Operation(summary = "Search and paginate users",
            description = """
                    Retrieves paginated list of users with optional fuzzy search.
                    """,
            operationId = "searchUsers")
    @ApiResponse(responseCode = "200",
            description = "Paginated user list retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class)))
    public Page<UserResponse> getUsers(
            @Parameter(description = "Search term (username or fullName)",
                    example = "john")
            @RequestParam(required = false, defaultValue = "") String search,

            @Parameter(description = "Page number (zero-based)",
                    example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Number of items per page",
                    example = "10")
            @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Searching users with term: '{}' (page: {}, size: {})", search, page, size);
        return userService.getUsers(search, page, size).map(UserResponse::toResponse);
    }
}