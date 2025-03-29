package com.jellyone.controller;

import com.jellyone.domain.enums.EventStatus;
import com.jellyone.domain.enums.TaskStatus;
import com.jellyone.util.AuthUtil;
import com.jellyone.web.request.EventRequest;
import com.jellyone.web.request.JwtRequest;
import com.jellyone.web.request.SignUpRequest;
import com.jellyone.web.request.TaskRequest;
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

import java.time.LocalDateTime;
import java.util.Collections;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskControllerTest {

    @LocalServerPort
    private int port;

    private static String jwtToken;

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
            createEvent();
        }
    }

    @Test
    @Order(1)
    void getAllTasksShouldReturnOk() {
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .queryParam("page", 0)
                .queryParam("pageSize", 10)
                .when()
                .get("/api/events/" + eventId + "/tasks")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(2)
    void createTaskShouldReturnCreated() {
        TaskRequest taskRequest = new TaskRequest(
                "Test Task",
                "testuser",
                TaskStatus.IN_PROGRESS,
                "This is a test task",
                100.0
        );

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(taskRequest)
                .when()
                .post("/api/events/" + eventId + "/tasks")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(TaskResponse.class);
    }

    @Test
    @Order(3)
    void getTaskByIdShouldReturnOk() {
        int taskId = 1; // Assuming task with ID 1 exists
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/events/" + eventId + "/tasks/" + taskId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(TaskResponse.class);
    }

    @Test
    @Order(4)
    void getTaskByIdNotFoundShouldReturnNotFound() {
        int taskId = 9999; // Non-existing task ID
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/api/events/" + eventId + "/tasks/" + taskId)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Order(5)
    void updateTaskShouldReturnOk() {
        int taskId = 1; // Assuming task with ID 1 exists
        TaskRequest taskRequest = new TaskRequest(
                "Updated Task",
                "testuser",
                TaskStatus.DONE,
                "This is an updated task",
                200.0
        );

        RestAssured.given()
                .auth().oauth2(jwtToken)
                .contentType(ContentType.JSON)
                .body(taskRequest)
                .when()
                .put("/api/events/" + eventId + "/tasks/" + taskId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract()
                .as(TaskResponse.class);
    }

    @Test
    @Order(6)
    void deleteTaskShouldReturnNoContent() {
        int taskId = 1; // Assuming task with ID 1 exists
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/api/events/" + eventId + "/tasks/" + taskId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Order(7)
    void deleteTaskNotFoundShouldReturnOk() {
        int taskId = 9999; // Non-existing task ID
        RestAssured.given()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/api/events/" + eventId + "/tasks/" + taskId)
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
}
