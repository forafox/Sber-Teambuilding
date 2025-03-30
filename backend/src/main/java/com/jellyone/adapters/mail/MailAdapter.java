package com.jellyone.adapters.mail;

import com.jellyone.mail.api.MailApi;
import com.jellyone.mail.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Profile("mail")
public class MailAdapter implements MailApi {

    @Override
    public double getTotalSum(Long eventId) {
        return 0.53453;
    }

    @Override
    public double getSumOwedToUser(Long eventId, Long userId) {
        return -1.2322;
    }

    @Override
    public double getSumSpentByUser(Long eventId, Long userId) {
        return -0.23232;
    }

    @Override
    public HashMap<UserDTO, Double> getUserDebts(Long eventId, Long userId) {
        return null;
    }
}
