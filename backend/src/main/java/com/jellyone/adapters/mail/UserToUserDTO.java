package com.jellyone.adapters.mail;

import com.jellyone.domain.User;
import com.jellyone.mail.dto.UserDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mail")
public class UserToUserDTO {
    public UserDTO userToUserDTO(User user){
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDTO(id, name, email);
    }
}