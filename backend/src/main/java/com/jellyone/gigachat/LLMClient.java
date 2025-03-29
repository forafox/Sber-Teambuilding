package com.jellyone.gigachat;

import com.jellyone.gigachat.dto.LLMResponse;

public interface LLMClient {
    LLMResponse generateEvent(String prompt);
}
