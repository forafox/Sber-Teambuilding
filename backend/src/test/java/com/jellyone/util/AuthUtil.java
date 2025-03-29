package com.jellyone.util;


import com.jellyone.web.request.JwtRequest;
import com.jellyone.web.request.SignUpRequest;
import com.jellyone.web.response.JwtResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.http.HttpStatus;

public class AuthUtil {

    public static String registerTestUser(String baseUri) {
        SignUpRequest signUpRequest = new SignUpRequest(
                "testuser",
                "password",
                "Test User",
                "test@test.com"
        );

        RestAssured.given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(signUpRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(HttpStatus.OK.value());

        return loginUserAndReturnAccessToken(baseUri, "testuser", "password");
    }

    public static String loginUserAndReturnAccessToken(String baseUri, String username, String password) {
        JwtRequest loginRequest = new JwtRequest(username, password);
        JwtResponse response = RestAssured.given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(JwtResponse.class);

        return response.accessToken();
    }

    public static String loginUserAndReturnRefreshToken(String baseUri, String username, String password) {
        JwtRequest loginRequest = new JwtRequest(username, password);
        JwtResponse response = RestAssured.given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .accept(ContentType.JSON)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(JwtResponse.class);

        return response.refreshToken();
    }
}