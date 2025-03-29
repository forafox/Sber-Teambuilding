package com.jellyone.controller;

import com.jellyone.service.UserService;
import com.jellyone.web.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "User Management")
@SecurityRequirement(name = "JWT")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current user")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user successfully retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public UserResponse me(Principal principal) {
        log.info("Received request to get current user");
        return UserResponse.toResponse(userService.getByUsername(principal.getName()));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by id", description = "Get user by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.class), mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public UserResponse getUserById(@PathVariable Long id) {
        log.info("Received request to get user by id");
        return UserResponse.toResponse(userService.getById(id));
    }

    @GetMapping("/users")
    @Operation(
            summary = "Get users with pagination and search",
            description = "Get paginated users with optional fuzzy search by fullName or username"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users successfully retrieved",
                    content = @Content(schema = @Schema(implementation = Page.class), mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Page<UserResponse> getUsers(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("Received request to get users");
        return userService.getUsers(search, page, size).map(UserResponse::toResponse);
    }
}