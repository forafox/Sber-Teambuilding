package com.jellyone.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionService sessionService;

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("Received: {}", payload);

        sessionService.sendMessageToSession(session, "Echo: " + payload);
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        sessionService.addSession(session);
        log.info("Connection established: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        sessionService.removeSession(session);
        log.info("Connection closed: {}", session.getId());
    }
}