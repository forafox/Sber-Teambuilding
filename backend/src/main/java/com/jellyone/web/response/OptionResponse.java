package com.jellyone.web.response;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OptionResponse(
        @NotNull(message = "Id must not be null")
        Long id,
        @NotNull
        String title,
        @NotNull(message = "Voters must not be null")
        List<UserResponse> voters
) {

    public static OptionResponse toResponse(com.jellyone.domain.Option option) {
        return new OptionResponse(
                option.getId(),
                option.getTitle(),
                option.getVoters().stream().map(UserResponse::toResponse).toList()
        );
    }
}
