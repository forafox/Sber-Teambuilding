package com.jellyone.gigachat;

import chat.giga.client.GigaChatClient;
import chat.giga.client.GigaChatClientImpl;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.JdkHttpClient;
import chat.giga.http.client.JdkHttpClientBuilder;
import chat.giga.http.client.SSL;
import chat.giga.model.Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Getter
@Configuration
public class GigachatConfig {

    private static final Logger log = LoggerFactory.getLogger(GigachatConfig.class);
    @Value("${gigachat.api.authorizationKey}")
    private String authorizationKey;
    @Value("${gigachat.api.clientId}")
    private String clientId;
    @Bean
    public GigaChatClientImpl gigachatClientImpl(GigachatConfig config, ObjectMapper objectMapper) {
        log.info("Authorization client: {}, key: {}", clientId, authorizationKey);

        return GigaChatClient.builder()
                .verifySslCerts(false)
                .logRequests(true)
                .logResponses(true)
                .connectTimeout(120)
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey(authorizationKey)
                                .build())
                        .build())
                .build();
    }

}