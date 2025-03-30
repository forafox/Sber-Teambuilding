package com.jellyone.adapters.mail;

import com.jellyone.domain.User;
import com.jellyone.mail.dto.UserDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Конвертер пользователей в DTO-объекты.
 */
@Component
@Profile("mail")
public class UserToUserDTO {

    /**
     * Преобразует пользователя в DTO.
     *
     * @param user объект пользователя
     * @return объект UserDTO
     */
    public UserDTO userToUserDTO(User user){
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDTO(id, name, email);
    }

    /**
     * Преобразует список пользователей в список DTO.
     *
     * @param users список пользователей
     * @return список объектов UserDTO
     */
    public List<UserDTO> userToUserDTO(List<User> users){
        return users.stream().map(this::userToUserDTO).collect(Collectors.toList());
    }
}