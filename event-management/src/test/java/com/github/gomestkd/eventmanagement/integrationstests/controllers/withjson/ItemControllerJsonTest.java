package com.github.gomestkd.eventmanagement.integrationstests.controllers.withjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.gomestkd.eventmanagement.config.TestConfig;
import com.github.gomestkd.eventmanagement.integrationstests.dto.AccountCredentialsDTO;
import com.github.gomestkd.eventmanagement.integrationstests.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.integrationstests.dto.TokenDTO;
import com.github.gomestkd.eventmanagement.integrationstests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.swagger.v3.core.filter.SpecFilter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static io.restassured.RestAssured.given;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=" + TestConfig.SERVER_PORT}
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerJsonTest extends AbstractIntegrationTest {
    private static RequestSpecification requestSpecification;
    private static ObjectMapper objectMapper;

    private static List<ItemDTO> itemDTOList;
    private static ItemDTO itemDTO;
    private static ItemDTO itemDTO2;
    private static ItemDTO itemDTO3;
    private static ItemDTO itemDTO4;

    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        itemDTOList = new ArrayList<>();
        itemDTO = new ItemDTO();
        itemDTO2 = new ItemDTO();
        itemDTO3 = new ItemDTO();
        itemDTO4 = new ItemDTO();

        tokenDTO = new TokenDTO();
    }

    @Test
    @Order(0)
    @DisplayName("Test signin: Should successfully authenticate and return access/refresh tokens")
    void signin() {
        AccountCredentialsDTO credentialsDTO = new AccountCredentialsDTO(
                "gomes", "admin123"
        );

        tokenDTO = given().basePath("/auth/signin")
                .port(TestConfig.SERVER_PORT)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(credentialsDTO)
                .when().post()
                .then().statusCode(200)
                .extract().body().as(TokenDTO.class);

        requestSpecification = new RequestSpecBuilder()
                .addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
                .setBasePath("/api/v1/items")
                .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        Assertions.assertNotNull(tokenDTO.getAccessToken(), "Access token should not be null");
        Assertions.assertNotNull(tokenDTO.getRefreshToken(), "Refresh token should not be null");
    }

    @Test
    @Order(1)
    @DisplayName("Test createTest: Should create a new person when provided with valid data")
    void createTest() throws JsonProcessingException {
        mockItems();

        String content = given(requestSpecification)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(itemDTOList)
                .when().post()
                .then().statusCode(200)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract().body().asString();

        List<ItemDTO> createdItems = Collections.singletonList(objectMapper.readValue(content, ItemDTO.class));
        itemDTOList = createdItems;

        Assertions.assertNotNull(createdItems, "The returned item list should not be null");
        Assertions.assertEquals(4, createdItems.size(), "Expected 4 items in the list, but found " + createdItems.size());

        ItemDTO itemOne = createdItems.getFirst();
        Assertions.assertNotNull(itemOne.getId(), "Item one's ID should not be null");
        Assertions.assertEquals("Coca-Cola", itemOne.getName(), "Item one: Name mismatch");
        Assertions.assertEquals(7.0, itemOne.getPrice(), "Item one: Price mismatch");
        Assertions.assertEquals("600ml - Original", itemOne.getDescription(), "Item one: Description mismatch");

        ItemDTO itemTwo = createdItems.get(1);
        Assertions.assertNotNull(itemTwo.getId(), "Item two's ID should not be null");
        Assertions.assertEquals("Coca-Cola", itemTwo.getName(), "Item two: Name mismatch (should be Coca-Cola)");
        Assertions.assertEquals(12.0, itemTwo.getPrice(), "Item two: Price mismatch");
        Assertions.assertEquals("2L - Zero Sugar", itemTwo.getDescription(), "Item two: Description mismatch");

        ItemDTO itemThree = createdItems.get(2);
        Assertions.assertNotNull(itemThree.getId(), "Item three's ID should not be null");
        Assertions.assertEquals("Guarana", itemThree.getName(), "Item three: Name mismatch");
        Assertions.assertEquals(5.0, itemThree.getPrice(), "Item three: Price mismatch");
        Assertions.assertEquals("600ml - Zero Sugar", itemThree.getDescription(), "Item three: Description mismatch");

        ItemDTO itemFour = createdItems.get(3);
        Assertions.assertNotNull(itemFour.getId(), "Item four's ID should not be null");
        Assertions.assertEquals("Hot Dog", itemFour.getName(), "Item four: Name mismatch");
        Assertions.assertEquals(8.0, itemFour.getPrice(), "Item four: Price mismatch");
        Assertions.assertEquals("Double complete - mustard and ketchup", itemFour.getDescription(), "Item four: Description mismatch");
    }

    private void mockItems() {
        itemDTO.setName("Coca-Cola");
        itemDTO.setPrice(7.0);
        itemDTO.setDescription("600ml - Original");

        itemDTO2.setName("Coca-Cola");
        itemDTO2.setPrice(12.0);
        itemDTO2.setDescription("2L - Zero Sugar");

        itemDTO3.setName("Guarana");
        itemDTO3.setPrice(5.0);
        itemDTO3.setDescription("600ml - Zero Sugar");

        itemDTO4.setName("Hot Dog");
        itemDTO4.setPrice(8.0);
        itemDTO4.setDescription("Double complete - mustard and ketchup");

        itemDTOList.add(itemDTO);
        itemDTOList.add(itemDTO2);
        itemDTOList.add(itemDTO3);
        itemDTOList.add(itemDTO4);
    }
}
