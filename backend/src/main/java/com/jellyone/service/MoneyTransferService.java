package com.jellyone.service;

import com.jellyone.domain.MoneyTransfer;
import com.jellyone.exception.ResourceNotFoundException;
import com.jellyone.repository.MoneyTransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyTransferService {
    private final MoneyTransferRepository moneyTransferRepository;
    private final EventService eventService;
    private final UserService userService;


    @Transactional
    public MoneyTransfer create(
            Double amount,
            Long senderId,
            Long recipientId,
            Long eventId
    ) {
        log.info("Create money transfer");
        MoneyTransfer moneyTransfer = MoneyTransfer.builder()
                .amount(amount)
                .sender(userService.getById(senderId))
                .recipient(userService.getById(recipientId))
                .event(eventService.getById(eventId))
                .build();
        moneyTransferRepository.save(moneyTransfer);
        return moneyTransfer;
    }

    @Transactional
    public MoneyTransfer update(
            Long id,
            Double amount,
            Long senderId,
            Long recipientId,
            Long eventId
    ) {
        log.info("Update money transfer");
        MoneyTransfer moneyTransfer = getById(id);
        moneyTransfer.setAmount(amount);
        moneyTransfer.setSender(userService.getById(senderId));
        moneyTransfer.setRecipient(userService.getById(recipientId));
        moneyTransfer.setEvent(eventService.getById(eventId));
        moneyTransferRepository.save(moneyTransfer);
        return moneyTransfer;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Delete money transfer");
        moneyTransferRepository.deleteById(id);
    }

    @Transactional
    public MoneyTransfer getById(Long id) {
        log.info("Try to get money transfer with id: {}", id);
        return moneyTransferRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Money transfer not found"));
    }

    @Transactional
    public Page<MoneyTransfer> getAllByEventId(Long eventId, int page, int size) {
        log.info("Try to get money transfer with eventId: {}", size);
        Pageable pageable = PageRequest.of(page, size);
        return moneyTransferRepository.findAllByEventId(eventId, pageable);
    }

}
