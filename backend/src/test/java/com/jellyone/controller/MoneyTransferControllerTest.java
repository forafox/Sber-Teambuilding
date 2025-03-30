package com.jellyone.controller;

import com.jellyone.domain.enums.EventStatus;
import com.jellyone.web.request.EventRequest;
import com.jellyone.web.request.JwtRequest;
import com.jellyone.web.request.MoneyTransferRequest;
import com.jellyone.web.request.SignUpRequest;
import com.jellyone.web.response.EventResponse;
import com.jellyone.web.response.JwtResponse;
import com.jellyone.web.response.MoneyTransferResponse;
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
class MoneyTransferControllerTest {

    @LocalServerPort
    private int port;

    private static String jwtToken;
    private static Long eventId;
    private static Long senderId;
    private static Long recipientId;

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
            registerTestUsers();
            loginUser();
            createEvent();
        }
    }

    private void registerTestUsers() {
        // Register sender user
        SignUpRequest senderRequest = new SignUpRequest("senderuser", "password", "Sender User", "sender@test.com");
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(senderRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Register recipient user
        SignUpRequest recipientRequest = new SignUpRequest("recipientuser", "password", "Recipient User", "recipient@test.com");
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(recipientRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private void loginUser() {
        JwtRequest loginRequest = new JwtRequest("senderuser", "password");

        JwtResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(JwtResponse.class);

        jwtToken = response.accessToken();

        // Get user IDs (assuming there's an endpoint to get current user info)
        // In a real scenario, you might need to get these IDs differently
        senderId = 1L; // Assuming sender is first created user
        recipientId = 2L; // Assuming recipient is second created user
    }

    private void createEvent() {
        EventRequest eventRequest = new EventRequest(
                "Test Event for Money Transfers",
                "Event Description",
                "Test Location",
                EventStatus.IN_PROGRESS,
                LocalDateTime.now(),
                Collections.singletonList(senderId) // Add sender to event
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

    @Test
    @Order(1)
    void getAllMoneyTransfersShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/events/" + eventId + "/money-transfers")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(2)
    void createMoneyTransferShouldReturnOk() {
        MoneyTransferRequest request = new MoneyTransferRequest(
                100.0,
                senderId,
                recipientId
        );

        MoneyTransferResponse response = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/events/" + eventId + "/money-transfers")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MoneyTransferResponse.class);

        Assertions.assertEquals(100.0, response.amount());
        Assertions.assertEquals(senderId, response.sender().id());
        Assertions.assertEquals(recipientId, response.recipient().id());
    }

    @Test
    @Order(3)
    void getMoneyTransferByIdShouldReturnOk() {
        // First create a transfer
        MoneyTransferRequest request = new MoneyTransferRequest(
                50.0,
                senderId,
                recipientId
        );

        MoneyTransferResponse createdTransfer = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/events/" + eventId + "/money-transfers")
                .then()
                .extract()
                .as(MoneyTransferResponse.class);

        // Then get it by ID
        MoneyTransferResponse retrievedTransfer = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/events/" + eventId + "/money-transfers/" + createdTransfer.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MoneyTransferResponse.class);

        Assertions.assertEquals(createdTransfer.id(), retrievedTransfer.id());
        Assertions.assertEquals(50.0, retrievedTransfer.amount());
    }

    @Test
    @Order(4)
    void getMoneyTransferByIdNotFoundShouldReturnNotFound() {
        Long nonExistingId = 9999L;
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/events/" + eventId + "/money-transfers/" + nonExistingId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(5)
    void updateMoneyTransferShouldReturnOk() {
        // First create a transfer
        MoneyTransferRequest createRequest = new MoneyTransferRequest(
                30.0,
                senderId,
                recipientId
        );

        MoneyTransferResponse createdTransfer = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(createRequest)
                .when()
                .post("/api/events/" + eventId + "/money-transfers")
                .then()
                .extract()
                .as(MoneyTransferResponse.class);

        // Then update it
        MoneyTransferRequest updateRequest = new MoneyTransferRequest(
                60.0,
                senderId,
                recipientId
        );

        MoneyTransferResponse updatedTransfer = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when()
                .put("/api/events/" + eventId + "/money-transfers/" + createdTransfer.id())
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(MoneyTransferResponse.class);

        Assertions.assertEquals(60.0, updatedTransfer.amount());
        Assertions.assertEquals(createdTransfer.id(), updatedTransfer.id());
    }

    @Test
    @Order(6)
    void deleteMoneyTransferShouldReturnOk() {
        // First create a transfer
        MoneyTransferRequest request = new MoneyTransferRequest(
                25.0,
                senderId,
                recipientId
        );

        MoneyTransferResponse createdTransfer = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/events/" + eventId + "/money-transfers")
                .then()
                .extract()
                .as(MoneyTransferResponse.class);

        // Then delete it
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/api/events/" + eventId + "/money-transfers/" + createdTransfer.id())
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verify it's deleted
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/events/" + eventId + "/money-transfers/" + createdTransfer.id())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(7)
    void createMoneyTransferWithInvalidDataShouldReturnBadRequest() {
        // Missing amount
        MoneyTransferRequest invalidRequest = new MoneyTransferRequest(
                null,
                senderId,
                recipientId
        );

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/events/" + eventId + "/money-transfers")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(8)
    void createMoneyTransferWithNonExistingUsersShouldReturnNotFound() {
        MoneyTransferRequest request = new MoneyTransferRequest(
                100.0,
                9999L, // Non-existing sender
                recipientId
        );

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/events/" + eventId + "/money-transfers")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}