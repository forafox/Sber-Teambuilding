package com.jellyone.adapters.telegram;

import com.jellyone.domain.Event;
import com.jellyone.telegram.bot.dto.EventDTO;
import com.jellyone.telegram.bot.dto.ParticipantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventToEventDTO {
    private final ParticipantToParticipantDTO participant;

    public EventDTO eventToEventDTO(Event event){
        Long id = event.getId();
        String title = event.getTitle();
        List<ParticipantDTO> participants = new ArrayList<>(!event.getParticipants().isEmpty()
                ? event.getParticipants().stream()
                .map(participant::participantToParticipantDTO)
                .toList()
                : List.of());
        participants.add(participant.participantToParticipantDTO(event.getAuthor()));

        return new EventDTO(id, title, participants);
    }
}
