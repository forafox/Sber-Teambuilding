package com.jellyone.controller;

import com.jellyone.util.AuthUtil;
import com.jellyone.web.request.EventRequest;
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
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventControllerTest {

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

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        if (jwtToken == null) {
            jwtToken = AuthUtil.registerTestUser(RestAssured.baseURI);
        }
    }

    @Test
    @Order(1)
    void getAllEventsShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/events")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(2)
    void createEventShouldReturnCreated() {
        EventRequest eventRequest = new EventRequest(
                "Test Event",
                "Test Event Description",
                "Test Event Location",
                LocalDateTime.now(),
                Collections.singletonList(1L) // Assuming user with ID 1 exists
        );

        RestAssured.given()
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
    }

    @Test
    @Order(3)
    void getEventByIdShouldReturnOk() {
        int eventId = 1; // Assuming event with ID 1 exists
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/events/" + eventId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(EventResponse.class);
    }

    @Test
    @Order(4)
    void getEventByIdNotFoundShouldReturnNotFound() {
        int eventId = 9999; // Non-existing event ID
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/events/" + eventId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(5)
    void updateEventShouldReturnOk() {
        int eventId = 1; // Assuming event with ID 1 exists
        EventRequest eventRequest = new EventRequest(
                "Updated Event",
                "Updated Event Description",
                "Updated Event Location",
                LocalDateTime.now(),
                Collections.singletonList(1L) // Assuming user with ID 1 exists
        );

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(eventRequest)
                .when()
                .put("/api/events/" + eventId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(EventResponse.class);
    }

    @Test
    @Order(6)
    void updateEventParticipantsShouldConflict() {
        int eventId = 1;
        List<Long> participants = List.of(1L);
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(participants)
                .when()
                .patch("/api/events/" + eventId + "/participants")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(7)
    void deleteEventShouldReturnNoContent() {
        int eventId = 1; // Assuming event with ID 1 exists
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/api/events/" + eventId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Order(8)
    void deleteEventNotFoundShouldReturnNotFound() {
        int eventId = 9999; // Non-existing event ID
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/api/events/" + eventId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
