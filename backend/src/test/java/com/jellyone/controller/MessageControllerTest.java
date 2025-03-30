package com.jellyone.controller;

import com.jellyone.domain.enums.EventStatus;
import com.jellyone.domain.enums.PollType;
import com.jellyone.util.AuthUtil;
import com.jellyone.web.request.*;
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
    private static Long pollId;
    private static Long optionId;

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
        MessageRequest messageRequest = new MessageRequest("Test Message", null, false, null);

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
        MessageUpdateRequest messageRequest = new MessageUpdateRequest(messageId, "Updated Message", null, false, null);

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
        MessageRequest messageRequest = new MessageRequest("Test Message", null, false, null);

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
        MessageRequest messageRequestWithReply = new MessageRequest("Test Message", messageId, false, null);

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

    @Test
    @Order(7)
    void createMessageWithPollShouldReturnOk() {
        MessageRequest messageRequest = new MessageRequest("Test Message",
                null,
                false,
                new PollRequest("Test Poll", PollType.SINGLE, List.of(new OptionRequest("Option 1"))));

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
        pollId = messageResponse.poll().id();
        optionId = messageResponse.poll().options().get(0).id();
    }

    @Test
    @Order(8)
    void updateMessageAndPollShouldReturnOk() {
        MessageUpdateRequest messageRequest = new MessageUpdateRequest(
                messageId,
                "Updated Message",
                null,
                false,
                new PollUpdateRequest(pollId, "Updated Poll", PollType.SINGLE, List.of(new OptionUpdateRequest(optionId, "Updated Option", List.of(1L)))));

        MessageResponse response = RestAssured.given()
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

        Assertions.assertEquals(messageId, response.id());
        Assertions.assertEquals("Updated Message", response.content());
        Assertions.assertEquals("Updated Poll", response.poll().title());
        Assertions.assertEquals("Updated Option", response.poll().options().get(0).title());
        Assertions.assertEquals(1L, response.poll().options().get(0).voters().get(0).id());
    }

    @Test
    @Order(9)
    void setMessageReadShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .post("/api/chats/" + chatId + "/messages/" + messageId + "/read")
                .then()
                .statusCode(HttpStatus.OK.value());

        ChatResponse response = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/chats/" + chatId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(ChatResponse.class);

        Assertions.assertEquals(messageId, response.readMessages().get(1L).id());
    }

    private void createEvent() {
        EventRequest eventRequest = new EventRequest(
                "Test Event",
                "Test Event Description",
                "Test Event Location",
                EventStatus.IN_PROGRESS,
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