package com.jellyone.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantDTO {
    private final long id;
    private final String name;
}
