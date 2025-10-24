package com.github.gomestkd.eventmanagement.integrationstests.swagger;

import com.github.gomestkd.eventmanagement.config.TestConfig;
import com.github.gomestkd.eventmanagement.integrationstests.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
public class SwaggerIntegrationTest extends AbstractIntegrationTest {
    @LocalServerPort
    private int port = TestConfig.SERVER_PORT;

    @Test
    @DisplayName("Swagger UI: Should return the Swagger UI page successfully")
    void shouldDisplaySwaggerUIPage() {
        String content = given()
                .basePath("/swagger-ui/index.html").port(port)
                .when().get()
                .then().statusCode(200)
                .extract().body().asString();

        Assertions.assertTrue(content.contains("Swagger UI"));
    }
}
