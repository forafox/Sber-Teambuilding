package com.jellyone.adapters.mail;

import com.jellyone.domain.Task;
import com.jellyone.mail.dto.TaskDTO;
import com.jellyone.mail.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("mail")
public class MailTaskToTaskDTO {
    private final UserToUserDTO userToUserDTO;

    public TaskDTO taskToTaskDTO(Task task){
        long id = task.getId();
        String title = task.getTitle();
        String description = task.getDescription();
        double price = task.getExpenses();
        UserDTO author = userToUserDTO.userToUserDTO(task.getAuthor());

        return new TaskDTO(id, title, description, author, price);
    }

    public List<TaskDTO> taskToTaskDTO(List<Task> tasks){
        return tasks.stream().map(this::taskToTaskDTO).collect(Collectors.toList());
    }
}