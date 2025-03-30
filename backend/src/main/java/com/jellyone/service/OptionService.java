package com.jellyone.service;

import com.jellyone.domain.Option;
import com.jellyone.domain.User;
import com.jellyone.domain.enums.ServerChange;
import com.jellyone.repository.OptionRepository;
import com.jellyone.web.request.OptionRequest;
import com.jellyone.web.request.OptionUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final UserService userService;
    private final WebSocketSessionService webSocketSessionService;

    public Option create(String title) {
        log.info("Try to create option with title: {}", title);
        return optionRepository.save(new Option(0L, title, new ArrayList<>()));
    }

    public Option getById(Long id) {
        log.info("Try to get option with id: {}", id);
        return optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
    }

    public void changeVotersByVotersIds(List<Long> userIds, Long optionId) {
        log.info("Try to change voters by voters ids");
        Option option = getById(optionId);

        // Получаем текущих voters
        List<User> currentVoters = option.getVoters();
        List<Long> currentVoterIds = currentVoters.stream().map(User::getId).toList();

        // Удаляем пользователей, которые есть в текущих voters, но нет в новом списке
        List<User> votersToRemove = currentVoters.stream()
                .filter(user -> !userIds.contains(user.getId()))
                .toList();
        currentVoters.removeAll(votersToRemove);

        // Добавляем новых пользователей, которых еще нет в voters
        for (Long userId : userIds) {
            if (!currentVoterIds.contains(userId)) {
                option.getVoters().add(userService.getById(userId));
            }
        }

        webSocketSessionService.sendMessageToAll(ServerChange.CHATS_UPDATED.name());
    }

    public List<Option> createOptions(List<OptionRequest> optionRequest) {
        log.info("Try to create options");
        List<Option> options = new ArrayList<>();
        for (OptionRequest option : optionRequest) {
            options.add(create(option.title()));
        }
        return options;
    }

    public List<Option> updateOptions(List<OptionUpdateRequest> optionRequest) {
        log.info("Try to update options");
        List<Option> options = new ArrayList<>();
        for (OptionUpdateRequest option : optionRequest) {
            Option optionFromDB = getById(option.id());
            optionFromDB.setTitle(option.title());
            optionFromDB.getVoters().clear();
            optionFromDB.getVoters().addAll(option.voters().stream().map(userService::getById).toList());
            optionFromDB = optionRepository.save(optionFromDB);
            options.add(optionFromDB);
        }
        return options;
    }
}
