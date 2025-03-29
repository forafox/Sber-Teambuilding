package com.jellyone.adapters.telegram;

import com.jellyone.domain.User;
import com.jellyone.telegram.bot.dto.ParticipantDTO;
import org.springframework.stereotype.Component;

@Component
public class ParticipantToParticipantDTO {
    public ParticipantDTO participantToParticipantDTO(User user){
        String username = user.getUsername();
        Long id = user.getId();

        return new ParticipantDTO(id, username);
    }
}
