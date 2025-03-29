package com.jellyone.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class WebSocketSessionService {

    private final Set<WebSocketSession> sessions = new HashSet<>();

    public void addSession(WebSocketSession session) {
        log.info("add session {}", session);
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        log.info("remove session {}", session);
        sessions.remove(session);
    }

    public void sendMessageToAll(String message) {
        log.info("Send message {} to all sessions", message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
                }
            }
        }
    }

    public void sendMessageToSession(WebSocketSession session, String message) {
        log.info("send message {}", message);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
            }
        }
    }
}