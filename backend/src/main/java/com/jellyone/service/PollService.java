package com.jellyone.service;

import com.jellyone.domain.Option;
import com.jellyone.domain.Poll;
import com.jellyone.repository.PollRepository;
import com.jellyone.web.request.PollRequest;
import com.jellyone.web.request.PollUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final OptionService optionService;

    public Poll create(PollRequest pollRequest) {
        if (pollRequest == null) {
            return null;
        }
        List<Option> options = optionService.createOptions(pollRequest.options());
        log.info("Try to create poll with title: {}", pollRequest.title());
        return pollRepository.save(new Poll(0L, pollRequest.title(), pollRequest.pollType(), options));
    }


    public Poll getById(Long id) {
        log.info("Try to get poll with id: {}", id);
        return pollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Poll not found"));
    }

    public Poll update(PollUpdateRequest pollRequest) {
        if (pollRequest == null) {
            return null;
        }
        Poll poll = getById(pollRequest.id());
        log.info("Try to update options");
        List<Option> options = optionService.updateOptions(pollRequest.options());
        log.info("Try to update poll with title: {}", pollRequest.title());
        poll.setTitle(pollRequest.title());
        poll.setPollType(pollRequest.pollType());
        poll.getOptions().clear();
        poll.getOptions().addAll(options);
        return pollRepository.save(poll);
    }
}
