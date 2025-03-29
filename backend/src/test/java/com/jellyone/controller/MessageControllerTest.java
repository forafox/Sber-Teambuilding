package com.jellyone.controller;

import com.jellyone.util.AuthUtil;
import com.jellyone.web.request.EventRequest;
import com.jellyone.web.request.MessageRequest;
import com.jellyone.web.response.ChatResponse;
import com.jellyone.web.response.EventResponse;
import com.jellyone.web.response.MessageResponse;
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
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageControllerTest {

    @LocalServerPort
    private int port;

    private static String jwtToken;
    private static Long eventId;
    private static Long chatId;
    private static Long messageId;

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
            createChat();
        }
    }

    @Test
    @Order(1)
    void createMessageShouldReturnOk() {
        MessageRequest messageRequest = new MessageRequest("Test Message", null);

        MessageResponse messageResponse = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(messageRequest)
                .when()
                .post("/api/chats/" + chatId + "/messages")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MessageResponse.class);

        messageId = messageResponse.id();
    }

    @Test
    @Order(2)
    void getMessageByIdShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/chats/" + chatId + "/messages/" + messageId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MessageResponse.class);
    }

    @Test
    @Order(3)
    void getAllMessagesByChatIdShouldReturnOk() {
        List<MessageResponse> messages = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/chats/" + chatId + "/messages")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList(".", MessageResponse.class);

        Assertions.assertFalse(messages.isEmpty());
    }

    @Test
    @Order(4)
    void updateMessageShouldReturnOk() {
        MessageRequest messageRequest = new MessageRequest("Updated Message", null);

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(messageRequest)
                .when()
                .put("/api/chats/" + chatId + "/messages/" + messageId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MessageResponse.class);
    }

    @Test
    @Order(5)
    void deleteMessageShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/api/chats/" + chatId + "/messages/" + messageId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Order(6)
    void createReplyMessageShouldReturnOk() {
        MessageRequest messageRequest = new MessageRequest("Test Message", null);

        MessageResponse messageResponse = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(messageRequest)
                .when()
                .post("/api/chats/" + chatId + "/messages")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MessageResponse.class);

        messageId = messageResponse.id();
        MessageRequest messageRequestWithReply = new MessageRequest("Test Message", messageId);

        MessageResponse messageResponseWithReply = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(messageRequestWithReply)
                .when()
                .post("/api/chats/" + chatId + "/messages")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MessageResponse.class);

        Assertions.assertEquals(messageId, messageResponseWithReply.replyToMessageId());
    }

    private void createEvent() {
        EventRequest eventRequest = new EventRequest(
                "Test Event",
                LocalDateTime.now(),
                Collections.singletonList(1L) // Assuming user with ID 1 exists
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

        eventId = eventResponse.id();
    }

    private void createChat() {
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
}