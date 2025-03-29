package com.jellyone.controller;

import com.jellyone.util.AuthUtil;
import com.jellyone.web.request.TelegramUserRequest;
import com.jellyone.web.response.TelegramUserResponse;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TelegramUserControllerTest {

    @LocalServerPort
    private int port;

    private static String jwtToken;
    private static Long createdTelegramUserId;

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
    void createTelegramUserShouldReturnOk() {
        TelegramUserRequest request = new TelegramUserRequest("test_telegram_user");

        TelegramUserResponse response = RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/telegram-users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(TelegramUserResponse.class);

        createdTelegramUserId = response.id();
        Assertions.assertNotNull(createdTelegramUserId);
        Assertions.assertEquals("test_telegram_user", response.telegramUsername());
    }

    @Test
    @Order(2)
    void createTelegramUserWithDuplicateUsernameShouldReturnConflict() {
        // First create with unique username
        String uniqueUsername = "unique_telegram_user_" + System.currentTimeMillis();
        TelegramUserRequest firstRequest = new TelegramUserRequest(uniqueUsername);

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(firstRequest)
                .when()
                .post("/api/telegram-users")
                .then()
                .statusCode(HttpStatus.OK.value());

        // Then try to create with same username
        TelegramUserRequest duplicateRequest = new TelegramUserRequest(uniqueUsername);

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(duplicateRequest)
                .when()
                .post("/api/telegram-users")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(3)
    void createTelegramUserWithNullUsernameShouldReturnBadRequest() {
        TelegramUserRequest invalidRequest = new TelegramUserRequest(null);

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .when()
                .post("/api/telegram-users")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @Order(4)
    void createTelegramUserWithoutAuthShouldReturnUnauthorized() {
        TelegramUserRequest request = new TelegramUserRequest("unauthorized_test_user");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/telegram-users")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}