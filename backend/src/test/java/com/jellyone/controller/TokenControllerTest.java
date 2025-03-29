package com.jellyone.controller;

import com.jellyone.domain.enums.EventStatus;
import com.jellyone.exception.ErrorMessage;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TokenControllerTest {
    @LocalServerPort
    private int port;

    private static String jwtToken;
    private static String tokenWithEventId;
    private static Long eventId;

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
    void createToken() {
        createEvent();
        String token = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .post("/api/tokens/events/" + eventId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType("text/plain;charset=UTF-8")
                .extract()
                .body()
                .asString();

        tokenWithEventId = token;
        Assertions.assertNotNull(token);
    }

    @Test
    @Order(2)
    void verifyToken() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.TEXT)
                .body(tokenWithEventId)
                .when()
                .post("/api/tokens/verify")
                .then()
                .statusCode(HttpStatus.CONFLICT.value()) // Юзер уже в группе
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorMessage.class);
    }

    @Test
    @Order(3)
    void verifyTokenWithWrongToken() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(tokenWithEventId + "123")
                .when()
                .post("/api/tokens/verify")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(ErrorMessage.class);
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

        eventId = eventResponse.id();
    }
}