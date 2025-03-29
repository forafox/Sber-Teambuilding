package com.jellyone.web.response;

import com.jellyone.domain.Poll;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PollResponse(
        @NotNull(message = "Id must not be null")
        Long id,
        @NotNull(message = "Title must not be null")
        String title,
        @NotNull(message = "Poll type must not be null")
        String pollType,
        @NotNull(message = "Options must not be null")
        List<OptionResponse> options
) {

    public static PollResponse toResponse(Poll poll) {
        return new PollResponse(
                poll.getId(),
                poll.getTitle(),
                poll.getPollType().name(),
                poll.getOptions().stream().map(OptionResponse::toResponse).toList()
        );
    }
}
