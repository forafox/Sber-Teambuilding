package com.jellyone.controller;

import com.jellyone.domain.enums.EventStatus;
import com.jellyone.util.AuthUtil;
import com.jellyone.web.request.EventRequest;
import com.jellyone.web.response.ChatResponse;
import com.jellyone.web.response.EventResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatControllerTest {

    @LocalServerPort
    private int port;

    private static String jwtToken;
    private static Long chatIdFromEvent;
    private static Long chatId;

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        if (jwtToken == null) {
            jwtToken = AuthUtil.registerTestUser(RestAssured.baseURI);
            createEvent();
        }
    }

    @Test
    @Order(1)
    void createChatShouldReturnOk() {
        ChatResponse chatResponse = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .post("/api/chats")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(ChatResponse.class);

        chatId = chatResponse.id();
    }

    @Test
    @Order(2)
    void getChatByIdShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/chats/" + chatIdFromEvent)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(ChatResponse.class);
    }

    @Test
    @Order(4)
    void deleteChatShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/api/chats/" + chatIdFromEvent)
                .then()
                .statusCode(HttpStatus.OK.value());
    }


    private void createEvent() {
        EventRequest eventRequest = new EventRequest(
                "Test Event",
                "Test Event Description",
                "Test Event Location",
                EventStatus.IN_PROGRESS,
                LocalDateTime.now(),
                Collections.singletonList(1L)
        );

        EventResponse eventResponse = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(eventRequest)
                .when()
                .post("/api/events")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(EventResponse.class);

        chatIdFromEvent = eventResponse.chatId();
    }
}