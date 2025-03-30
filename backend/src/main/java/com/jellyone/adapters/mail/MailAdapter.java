package com.jellyone.adapters.mail;

import com.jellyone.domain.Task;
import com.jellyone.mail.api.MailApi;
import com.jellyone.mail.dto.UserDTO;
import com.jellyone.service.EventService;
import com.jellyone.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Profile("mail")
public class MailAdapter implements MailApi {

    private final EventService eventService;
    private final TaskService taskService;

    @Override
    public double getTotalSum(Long eventId) {
        List<Task> tasks = taskService.getAllByEventID(eventId);
        return tasks.stream().mapToDouble(Task::getExpenses).sum();
    }

    @Override
    public double getSumOwedToUser(Long eventId, Long userId) {
        return 1235;
    }

    @Override
    public double getSumSpentByUser(Long eventId, Long userId) {
        List<Task> tasks = taskService.getAllByEventID(eventId);
        double sum = 0;
        for (Task task : tasks) {
            if (task.getAssignee() != null && task.getAssignee().getId().equals(userId)) {
                sum += task.getExpenses();
            }
        }
        return sum;
    }
}
