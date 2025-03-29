package com.jellyone.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private long id;
    private String name;
    private String email;
}
