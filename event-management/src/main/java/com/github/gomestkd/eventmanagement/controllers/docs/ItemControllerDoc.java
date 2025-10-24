package com.github.gomestkd.eventmanagement.controllers.docs;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface ItemControllerDoc {
    @Operation(
            summary = "Find all Items - Paginated",
            description = "Find all Items with pagination support.",
            tags = { "Items" },
            operationId = "findAllItems",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Items",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(
                                                    schema = @Schema(implementation = ItemDTO.class)
                                            )
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "204", description = "No Content - No Items found", content = @Content),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid parameters supplied",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication is required and has failed or has not yet been provided",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - The request was valid, but the server is refusing action",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - The requested resource could not be found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = @Content
                    )
            }
    )
    ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findAll(
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(description = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "asc")
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    );

    @Operation(
            summary = "Get item by ID",
            description = "Retrieves details of a specific item by ID.",
            tags = {"Items"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item found", content = @Content(schema = @Schema(implementation = ItemDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Item not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    ItemDTO findById(
        @Parameter(description = "ID of the item to be retrieved", example = "1")
        @PathVariable("id") Long id
    );

    @Operation(
            summary = "Finds an Item by Name",
            description = "Finds an Item by its name.",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            description = "Item found successfully",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ItemDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findByName(
            @Parameter(description = "Name of the item", example = "Sample Item")
            @PathVariable("name") String name,
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(description = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "asc")
            @RequestParam(value = "sort", defaultValue = "asc") String sort
    );

    @Operation(
            summary = "FInds an Item by Description",
            description = "Finds an Item by its description.",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            description = "Item found successfully",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ItemDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findByDescription(
            @Parameter(description = "Description of the item", example = "600ml")
            @PathVariable("description") String description,
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(description = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "asc")
            @RequestParam(value = "sort", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Finds an Item by Price Range",
            description = "Finds an Item by its price range.",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            description = "Item found successfully",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ItemDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findByPriceRange(
            @Parameter(description = "Minimum price of the item", example = "10.0")
            @PathVariable("minPrice") String minPrice,
            @Parameter(description = "Maximum price of the item", example = "100.0")
            @PathVariable("maxPrice") String maxPrice,
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(description = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "asc")
            @RequestParam(value = "sort", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Exports Items in PDF, CSV or XLSX format",
            description = "Exports all Items in a PDF, CSV or XLSX file",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "PDF file created successfully",
                            content = {
                                    @Content(
                                            mediaType = "application/pdf",
                                            schema = @Schema(type = "string", format = "binary")
                                    ),
                                    @Content(
                                            mediaType = "text/csv",
                                            schema = @Schema(type = "string", format = "binary")
                                    ),
                                    @Content(
                                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                            schema = @Schema(type = "string", format = "binary")
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "204", description = "No Content", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<Resource> exportPage(
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(description = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.", example = "asc")
            @RequestParam(value = "sort", defaultValue = "asc") String sort,
            HttpServletRequest request
    );

    @Operation(
            summary = "Exports an Item by ID in PDF format",
            description = "Exports an Item by its ID in a PDF file",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "PDF file created successfully",
                            content = {
                                    @Content(
                                            mediaType = "application/pdf",
                                            schema = @Schema(type = "string", format = "binary")
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<Resource> exportItem(
            @Parameter(description = "ID of the item", example = "1")
            @PathVariable("id") Long id,
            HttpServletRequest request
    );

    @Operation(
            summary = "Mass Creation of Items",
            description = "Creates multiple items by uploading a CSV or XLSX file.",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            description = "Items created successfully",
                            responseCode = "201",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = ItemDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    Set<ItemDTO> massCreation(
            @Parameter(description = "CSV or XLSX file containing people data", required = true)
            MultipartFile file
    );

    @Operation(
            summary = "Adds a new Item",
            description = "Adds a new item by passing in a JSON representation of the person.",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            description = "Item created successfully",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = ItemDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ItemDTO create(
        @Parameter(description = "JSON representation of the item to be updated", required = true)
        @RequestBody ItemDTO itemDTO
    );

    @Operation(
            summary = "Updates an existing Item",
            description = "Updates an existing item by passing in a JSON representation of the item.",
            tags = { "Items" },
            responses = {
                    @ApiResponse(
                            description = "Item updated successfully",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ItemDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ItemDTO update(
        @Parameter(description = "JSON representation of the item to be updated", required = true)
        @RequestBody ItemDTO itemDTO
    );

    @Operation(
            summary = "Deletes an Item by ID",
            description = "Deletes an item by its ID.",
            tags = { "Items" },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Item deleted successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    ResponseEntity<?> delete(
        @Parameter(description = "ID of the item to be deleted", example = "1")
        @PathVariable("id") Long id
    );
}
