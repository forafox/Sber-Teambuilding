package com.jellyone.controller;

import com.jellyone.util.AuthUtil;
import com.jellyone.web.request.JwtRequest;
import com.jellyone.web.request.SignUpRequest;
import com.jellyone.web.response.JwtResponse;
import com.jellyone.web.response.UserResponse;
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
class UserControllerTest {

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
    void getCurrentUserShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(UserResponse.class);
    }

    @Test
    void getUserByIdShouldReturnOk() {
        int userId = 1;
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/users/" + userId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(UserResponse.class);
    }

    @Test
    void getUserByIdNotFoundShouldReturnNotFound() {
        int userId = 9999;
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/users/" + userId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getUsersWithPaginationShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .queryParam("search", "test")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when()
                .get("/api/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
    }

    @Test
    void getUsersWithoutAuthShouldReturnUnauthorized() {
        RestAssured.given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

}
