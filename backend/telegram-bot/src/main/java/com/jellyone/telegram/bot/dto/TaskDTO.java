package com.jellyone.telegram.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskDTO implements Printable {
    private long id;
    private String title;
    private String description;
    private double expenses;
    private long assignedID;
    private long authorID;
    private long eventID;
    private String url;

    public TaskDTO(long eventID, long authorID) {
        this.eventID = eventID;
        this.authorID = authorID;
    }
}
