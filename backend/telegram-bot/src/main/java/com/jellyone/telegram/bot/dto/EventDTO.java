package com.jellyone.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class EventDTO implements Printable {
    private long id;
    private String title;
    private List<ParticipantDTO> participants;
}
