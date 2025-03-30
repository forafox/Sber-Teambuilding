package com.jellyone.controller;

import com.jellyone.domain.enums.EventStatus;
import com.jellyone.domain.enums.TaskStatus;
import com.jellyone.util.AuthUtil;
import com.jellyone.web.response.EventResponse;
import com.jellyone.web.response.TaskResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class TemplateControllerTest {

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
    void getAllTemplatesEventShouldReturnAllMockEvents() {
        List<EventResponse> responses = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/templates/events")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath()
                .getList(".", EventResponse.class);

        assertThat(responses).hasSize(3);
        assertThat(responses).extracting(EventResponse::title)
                .containsExactlyInAnyOrder("Шашлыки", "День рождения в боулинге", "Настольные игры");
    }

    @Test
    void getTemplateEventByIdShouldReturnCorrectEvent() {
        EventResponse response = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/templates/events/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(EventResponse.class);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Шашлыки");
        assertThat(response.status()).isEqualTo(EventStatus.IN_PROGRESS);
    }

    @Test
    void getTemplateEventByIdNotFoundShouldReturnNotFound() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/templates/events/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllTasksByEventIdShouldReturnCorrectTasks() {
        List<TaskResponse> responses = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/templates/events/1/tasks")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath()
                .getList(".", TaskResponse.class);

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(TaskResponse::title)
                .containsExactlyInAnyOrder("Купить мясо", "Привезти мангал");
    }

    @Test
    void getTemplateTaskByIdShouldReturnCorrectTask() {
        TaskResponse response = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/templates/events/1/tasks/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(TaskResponse.class);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Купить мясо");
        assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response.expenses()).isEqualTo(2500.0);
    }

    @Test
    void getTemplateTaskByIdNotFoundShouldReturnNotFound() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/templates/events/1/tasks/999")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void applyTemplateShouldCreateTasksWithCorrectParameters() {
        EventResponse eventResponse = RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .post("/api/templates/events/1")
                .then()
                .extract().as(EventResponse.class);

        assertThat(eventResponse.description()).isEqualTo("Выезд на природу, шашлыки и активные игры");
        assertThat(eventResponse.title()).isEqualTo("Шашлыки");
        assertThat(eventResponse.status()).isEqualTo(EventStatus.IN_PROGRESS);
    }
}