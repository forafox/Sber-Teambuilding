package com.jellyone.adapters.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!mail")
public class NoSenderService implements SenderService {
    @Override
    public void sendMail(String email, Long userId, Long eventId) {

    }

    @Override
    public void sendMail(Long userId, Long eventId) {

    }
}
