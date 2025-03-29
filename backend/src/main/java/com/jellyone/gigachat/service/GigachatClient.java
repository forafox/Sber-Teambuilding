package com.jellyone.gigachat.service;

import chat.giga.client.GigaChatClientImpl;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jellyone.gigachat.GigachatConfig;
import com.jellyone.gigachat.LLMClient;
import com.jellyone.gigachat.dto.LLMResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import chat.giga.client.GigaChatClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GigachatClient implements LLMClient {
    
    private static final Logger log = LoggerFactory.getLogger(GigachatClient.class);
    
    private final GigachatConfig config;
    private final ObjectMapper objectMapper;
    private final GigaChatClientImpl client;

    @SneakyThrows
    public LLMResponse generateEvent(String userPrompt) {
        log.info("Generating event with prompt: {}", userPrompt);
        
        ChatMessage systemMessage = ChatMessage.builder()
                .role(ChatMessage.Role.SYSTEM)
                .content("Ты профессиональный организатор мероприятий. Создай интересное мероприятие на основе запроса пользователя. " +
                        "Ответ должен быть в формате JSON со следующей структурой:\n" +
                        "{\n" +
                        "  \"title\": \"Название мероприятия\",\n" +
                        "  \"tasks\": [\"Задача 1\", \"Задача 2\", ...]\n" +
                        "}\n" +
                        "Убедись, что ответ строго соответствует этой структуре JSON.")
                .build();
        
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessage.Role.USER)
                .content(userPrompt)
                .build();
        
        CompletionRequest request = CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .messages(List.of(systemMessage, userMessage))
                .build();
        
        try {
            log.info("Request body: {}", objectMapper.writeValueAsString(request));
            
            var response = client.completions(request);
            log.info("Response received: {}", response);
            
            if (response != null && !response.choices().isEmpty()) {
                try {
                    LLMResponse llmResponse = objectMapper.readValue(
                        response.choices().get(0).message().content(),
                        LLMResponse.class
                    );
                    log.info("Successfully generated event: {}", llmResponse);
                    return llmResponse;
                } catch (Exception e) {
                    log.error("Failed to parse LLM response. Raw content: {}", 
                        response.choices().get(0).message().content(), e);
                    throw new RuntimeException("Failed to parse LLM response", e);
                }
            }
            
            log.error("Empty or invalid response received: {}", response);
            throw new RuntimeException("Failed to generate event description");
        } catch (Exception e) {
            log.error("Error during event generation request", e);
            throw e;
        }
    }
}