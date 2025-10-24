package com.github.gomestkd.eventmanagement.integrationstests.controllers.cors.withjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gomestkd.eventmanagement.config.TestConfig;
import com.github.gomestkd.eventmanagement.file.MediaTypes;
import com.github.gomestkd.eventmanagement.integrationstests.dto.AccountCredentialsDTO;
import com.github.gomestkd.eventmanagement.integrationstests.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.integrationstests.dto.TokenDTO;
import com.github.gomestkd.eventmanagement.integrationstests.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static io.restassured.RestAssured.given;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=" + TestConfig.SERVER_PORT}
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerCorsTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static ItemDTO itemDTO;
    private static TokenDTO tokenDTO;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        itemDTO = new ItemDTO();
        tokenDTO = new TokenDTO();
    }

    @Test
    @Order(0)
    @DisplayName("Should successfully sign in and initialize the authenticated specification")
    void signin() {
        AccountCredentialsDTO credentials = new AccountCredentialsDTO("gomes", "admin123");
        try {
            tokenDTO = given()
                    .basePath("/auth/signin").port(TestConfig.SERVER_PORT)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).body(credentials)
                    .when().post()
                    .then().statusCode(200)
                    .extract().as(TokenDTO.class);
        } catch (Exception e) {
            Assertions.fail("Signin failed with an exception", e);
        }
        Assertions.assertNotNull(tokenDTO.getAccessToken(), "Access token should not be null");

        specification = new RequestSpecBuilder()
                .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
                .addHeader(TestConfig.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenDTO.getAccessToken())
                .setBasePath("/api/v1/items")
                .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("create: Should create an item when data and origin are valid")
    void testCreate_withValidData() {
        try {
            mockItem();
            String content = given().spec(specification)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).body(itemDTO)
                    .when().post("/create")
                    .then().statusCode(200)
                    .extract().body().asString();

            ItemDTO createdItem = objectMapper.readValue(content, ItemDTO.class);
            itemDTO = createdItem;

            Assertions.assertNotNull(createdItem);
            Assertions.assertNotNull(createdItem.getId());
            Assertions.assertTrue(createdItem.getId() > 0);
            Assertions.assertEquals("Guarana", createdItem.getName());
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during item creation test", e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("findAll: Should return a paginated list of items")
    void testFindAll_shouldReturnPagedListOfItems() {
        try {
            String content = given(specification)
                    .queryParam("page", 0).queryParam("size", 5)
                    .when().get()
                    .then().statusCode(200)
                    .extract().body().asString();

            Assertions.assertNotNull(content, "Response content should not be null");
            Assertions.assertTrue(content.contains("\"_embedded\""), "Response should contain the '_embedded' object");
            Assertions.assertTrue(content.contains("\"items\""), "Response should contain a list of 'items'");
            Assertions.assertTrue(content.contains("_links"), "Response should contain HATEOAS links");
            Assertions.assertTrue(content.contains("\"page\""), "Response should contain page metadata");
            Assertions.assertTrue(content.contains("\"number\":0"), "Response should be for page number 0");
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findAll test", e);
        }
    }

    @Test
    @Order(3)
    @DisplayName("findById: Should return an item when ID is valid")
    void testFindById_whenIdIsValid() {
        try {
            String content = given().spec(specification)
                    .pathParam("id", itemDTO.getId())
                    .when().get("/findById/{id}")
                    .then().statusCode(200)
                    .extract().body().asString();

            ItemDTO foundItem = objectMapper.readValue(content, ItemDTO.class);
            Assertions.assertNotNull(foundItem);
            Assertions.assertEquals(itemDTO.getId(), foundItem.getId());
            Assertions.assertEquals("Guarana", foundItem.getName());
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findById test", e);
        }
    }

    @Test
    @Order(4)
    @DisplayName("findById: Should return 404 when ID does not exist")
    void testFindById_whenIdDoesNotExist() {
        given(specification)
                .pathParam("id", 9999L)
                .when().get("/findById/{id}")
                .then().statusCode(404);
    }

    @Test
    @Order(5)
    @DisplayName("findByName: Should return a paginated list of items matching the name")
    void testFindByName() {
        try {
            String content = given().spec(specification)
                    .pathParam("name", itemDTO.getName())
                    .when().get("/findByName/{name}")
                    .then().statusCode(200)
                    .extract().body().asString();

            Assertions.assertTrue(content.contains(itemDTO.getName()));
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findByName test", e);
        }
    }

    @Test
    @Order(6)
    @DisplayName("findByDescription: Should return a paginated list of items matching the description")
    void testFindByDescription() {
        try {
            String content = given().spec(specification)
                    .pathParam("description", itemDTO.getDescription())
                    .when().get("/findByDescription/{description}")
                    .then().statusCode(200)
                    .extract().body().asString();

            Assertions.assertTrue(content.contains(itemDTO.getDescription()));
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findByDescription test", e);
        }
    }

    @Test
    @Order(7)
    @DisplayName("findByPriceRange: Should return a paginated list for a valid price range")
    void testFindByPriceRange_whenRangeIsValid() {
        try {
            String content = given().spec(specification)
                    .pathParam("minPrice", "5.0")
                    .pathParam("maxPrice", "15.0")
                    .when().get("/findByPriceRange/{minPrice}/{maxPrice}")
                    .then().statusCode(200)
                    .extract().body().asString();

            Assertions.assertNotNull(content);
            Assertions.assertTrue(content.contains("\"_embedded\""), "Response should be a paged model");
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during findByPriceRange test", e);
        }
    }

    @Test
    @Order(8)
    @DisplayName("findByPriceRange: Should return 400 for an invalid price range")
    void testFindByPriceRange_whenRangeIsInvalid() {
        given(specification)
                .pathParam("minPrice", "invalid")
                .pathParam("maxPrice", "price")
                .when().get("/findByPriceRange/{minPrice}/{maxPrice}")
                .then().statusCode(400);
    }

    @Test
    @Order(9)
    @Disabled("PDF not implemented yet")
    @DisplayName("exportPage: Should return a PDF file for the item list")
    void testExportPage() {
        // Test logic remains here for when implementation is ready
    }

    @Test
    @Order(10)
    @Disabled("PDF not implemented yet")
    @DisplayName("exportItem: Should return a PDF file for a single item")
    void testExportItem() {
        // Test logic remains here for when implementation is ready
    }

    @Test
    @Order(11)
    @Disabled("CSV not implemented yet")
    @DisplayName("massCreation: Should create items from a CSV file upload")
    void testMassCreation_withCsvFile() {
        try {
            String csvContent = "name,description,price\nFanta,500ml,8.0\nSprite,500ml,7.5";
            File tempFile = Files.createTempFile("upload", ".csv").toFile();
            Files.writeString(tempFile.toPath(), csvContent);

            String content = given().spec(specification)
                    .multiPart("file", tempFile, "text/csv")
                    .when().post("/massCreation")
                    .then().statusCode(200)
                    .extract().body().asString();

            Set<ItemDTO> createdItems = objectMapper.readValue(content, new TypeReference<>() {});

            Assertions.assertEquals(2, createdItems.size());
            Assertions.assertTrue(createdItems.stream().anyMatch(item -> "Fanta".equals(item.getName())));
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during massCreation test", e);
        }
    }

    @Test
    @Order(12)
    @DisplayName("update: Should update an item when data is valid")
    void testUpdate_withValidData() {
        try {
            itemDTO.setName("Coca-Cola");
            itemDTO.setPrice(12.50);

            String content = given().spec(specification)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).body(itemDTO)
                    .when().put("/updateItem")
                    .then().statusCode(200)
                    .extract().body().asString();

            ItemDTO updatedItem = objectMapper.readValue(content, ItemDTO.class);
            itemDTO = updatedItem;

            Assertions.assertNotNull(updatedItem);
            Assertions.assertEquals("Coca-Cola", updatedItem.getName());
            Assertions.assertEquals(12.50, updatedItem.getPrice());
        } catch (Exception e) {
            Assertions.fail("An unexpected error occurred during update test", e);
        }
    }

    @Test
    @Order(13)
    @DisplayName("delete: Should return 204 when deleting an existing item")
    void testDelete_whenIdExists() {
        given(specification)
                .pathParam("id", itemDTO.getId())
                .when().delete("/{id}")
                .then().statusCode(204);
    }

    @Test
    @Order(14)
    @DisplayName("delete: Should return 404 when trying to find a deleted item")
    void testFindById_afterDeletion() {
        given(specification)
                .pathParam("id", itemDTO.getId())
                .when().get("/findById/{id}")
                .then().statusCode(404);
    }

    @Test
    @Order(15)
    @DisplayName("create: Should return 403 Forbidden when token is missing")
    void testCreate_withMissingToken() {
        mockItem();

        RequestSpecification specWithoutAuth = new RequestSpecBuilder()
                .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
                .setBasePath("/api/v1/items")
                .setPort(TestConfig.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        given(specWithoutAuth)
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(itemDTO)
                .when().post("/create")
                .then().statusCode(403);
    }

    private void mockItem() {
        itemDTO.setName("Guarana");
        itemDTO.setDescription("600ml - zero");
        itemDTO.setPrice(10.0);
    }
}

