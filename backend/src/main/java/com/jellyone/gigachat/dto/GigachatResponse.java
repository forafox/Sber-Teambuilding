package com.jellyone.gigachat.dto;

import lombok.Data;
import java.util.List;

@Data
public class GigachatResponse {
    private String id;
    private String object;
    private long created;
    private List<Choice> choices;
    
    @Data
    public static class Choice {
        private Message message;
        private int index;
        private String finish_reason;
    }
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
} 