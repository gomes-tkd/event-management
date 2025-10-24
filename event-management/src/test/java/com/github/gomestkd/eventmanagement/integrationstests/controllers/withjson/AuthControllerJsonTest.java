package com.github.gomestkd.eventmanagement.integrationstests.controllers.withjson;


import com.github.gomestkd.eventmanagement.config.TestConfig;
import com.github.gomestkd.eventmanagement.integrationstests.dto.AccountCredentialsDTO;
import com.github.gomestkd.eventmanagement.integrationstests.dto.TokenDTO;
import com.github.gomestkd.eventmanagement.integrationstests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=" + TestConfig.SERVER_PORT}
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerJsonTest extends AbstractIntegrationTest {

    private static TokenDTO tokenDto;

    @BeforeAll
    static void setUp() {
        tokenDto = new TokenDTO();
    }

    @Test
    @Order(1)
    @DisplayName("Test signin: Should signin successfully and return valid tokens")
    void signin() {
        AccountCredentialsDTO credentialsDTO = new AccountCredentialsDTO(
            "gomes", "admin123"
        );

        tokenDto = given()
                .basePath("/auth")
                .port(TestConfig.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentialsDTO)
                .when().post("/signin")
                .then().statusCode(200)
                .extract().body().as(TokenDTO.class);

        Assertions.assertNotNull(credentialsDTO);
        Assertions.assertNotNull(tokenDto.getAccessToken());
        Assertions.assertNotNull(tokenDto.getRefreshToken());
    }

    @Test
    @Order(2)
    @DisplayName("Test refresh: Should return new tokens when using a valid refresh token")
    void refreshToken() {
        tokenDto = given()
                .basePath("/auth/refresh")
                .port(TestConfig.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("username", tokenDto.getUsername())
                .header(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDto.getRefreshToken())
                .when()
                .put("{username}")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(TokenDTO.class);

        Assertions.assertNotNull(tokenDto.getAccessToken());
        Assertions.assertNotNull(tokenDto.getRefreshToken());
    }
}