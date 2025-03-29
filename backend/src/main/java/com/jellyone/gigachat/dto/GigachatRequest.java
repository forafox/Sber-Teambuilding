package com.jellyone.gigachat.dto;

import lombok.Data;
import java.util.List;

@Data
public class GigachatRequest {
    private String model;
    private List<Message> messages;
    private boolean stream;
    private int update_interval;
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
} 