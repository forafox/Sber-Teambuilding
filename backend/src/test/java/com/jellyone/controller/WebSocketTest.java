package com.jellyone.controller;

import com.jellyone.service.WebSocketHandler;
import com.jellyone.service.WebSocketSessionService;
import com.jellyone.util.AuthUtil;
import io.restassured.RestAssured;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebSocketTest {

    @LocalServerPort
    private int port;

    private static String jwtToken;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private WebSocketSessionService sessionService;

    @Autowired
    private WebSocketHandler webSocketHandler;

    private WebSocketClient webSocketClient;

    @BeforeEach
    void setUp() {
        webSocketClient = new StandardWebSocketClient();
        RestAssured.baseURI = "http://localhost:" + port;
        if (jwtToken == null) {
            jwtToken = AuthUtil.registerTestUser(RestAssured.baseURI);
        }
    }

    @Test
    void testWebSocketConnectionAndMessageHandling() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketSession session = webSocketClient.doHandshake(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
                System.out.println("Received message: " + message.getPayload());
                latch.countDown();
            }
        }, "ws://localhost:" + port + "/ws").get();

        session.sendMessage(new TextMessage("Hello, WebSocket!"));

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Сообщение не было получено");

        session.close();
    }

    @Test
    void testNotificationController() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketSession session = webSocketClient.doHandshake(new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
                System.out.println("Received notification: " + message.getPayload());
                latch.countDown();
            }
        }, "ws://localhost:" + port + "/ws").get();

        String notificationMessage = "Test notification";
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .port(port)
                .body(notificationMessage)
                .when()
                .post("/api/notify")
                .then()
                .statusCode(200);

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Уведомление не было получено");

        session.close();
    }
}