package com.jellyone.telegram.bot.services;

import com.jellyone.telegram.bot.dto.TaskDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Этот сервис хранит промежуточные значения для тасок
 * те юзер вводит тайтл для таски, но его нужно куда-то сохранить
 * здесь храню промежуточные значения для каждого пользователя в хешмапе
 *  @author Kirill Podkovyrin
 *  @version 1.0
 */
@Service
@Profile("telegram")
public class TaskSaveService {
    private final Map<Long, TaskDTO> dataTasks = new HashMap<>();
    public void save(long chatID, TaskDTO taskDTO) {
        dataTasks.put(chatID, taskDTO);
    }

    public TaskDTO get(long chatID) {
        return dataTasks.get(chatID);
    }

    public void remove(long chatID) {
        dataTasks.remove(chatID);
    }
}
