package com.jellyone.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDTO {
    private long id;
    private String title;
    private String description;
    private UserDTO user;
    private double price;
}
