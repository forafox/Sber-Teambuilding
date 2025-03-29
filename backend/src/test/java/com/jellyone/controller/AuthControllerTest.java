package com.jellyone.controller;

import com.jellyone.web.request.JwtRequest;
import com.jellyone.web.request.SignUpRequest;
import com.jellyone.web.response.JwtResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class AuthControllerTest {

    @LocalServerPort
    private int port;

    private static String jwtToken;
    private static String refreshToken;

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
            registerTestUser();
            loginUser();
        }
    }

    private void registerTestUser() {
        SignUpRequest signUpRequest = new SignUpRequest("testuser", "password", "Test User", "test1@test.com");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signUpRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private void loginUser() {
        JwtRequest loginRequest = new JwtRequest("testuser", "password");

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
        refreshToken = response.refreshToken();
    }

    @Test
    void refreshNoJwtTokenShouldReturnBadRequest() {
        RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void refreshBadJwtTokenShouldReturnUnauthorized() {
        String invalidToken = "eyJhbGciOiJIUzUxMiJ9.INVALID_TOKEN";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(invalidToken)
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void refreshWithoutAccessTokenWithBadRefreshTokenShouldReturnUnauthorized() {
        String badRefreshToken = "eyJhbGciOiJIUzUxMiJ9.INVALID_REFRESH_TOKEN";

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(badRefreshToken)
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void refreshWithoutAccessTokenWithGoodRefreshTokenShouldReturnOk() {
        JwtResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(refreshToken)
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(JwtResponse.class);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(response.refreshToken())
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void registerShouldBeAvailableWithoutJWT() {
        SignUpRequest signUpRequest = new SignUpRequest("testuser2", "password", "Test User", "test2@test.com");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signUpRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void loginWithIncorrectCredentialsShouldReturnUnauthorized() {
        JwtRequest loginRequest = new JwtRequest("wronguser", "wrongpassword");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void loginWithCorrectCredentialsShouldReturnOk() {
        JwtRequest loginRequest = new JwtRequest("testuser", "password");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void registerWithExistingUsernameShouldReturnConflict() {
        SignUpRequest signUpRequest = new SignUpRequest("testuser", "password", "Test User", "test@test.com");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(signUpRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }
}
