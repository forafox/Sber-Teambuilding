package com.jellyone.adapters.telegram;

import com.jellyone.domain.Task;
import com.jellyone.telegram.bot.dto.TaskDTO;
import org.springframework.stereotype.Component;

@Component
public class TelegramTaskToTaskDTO {
    public TaskDTO taskToTaskDTO(Task task){
        long id = task.getId();
        String title = task.getTitle();
        String description = task.getDescription();
        double price = task.getExpenses();
        long assignee = task.getAssignee().getId();
        long author = task.getAuthor().getId();
        long eventId = task.getEvent().getId();

        return new TaskDTO(id, title, description, price, assignee, author, eventId);
    }
}
