package com.github.gomestkd.eventmanagement.controllers.docs;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.file.MediaTypes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface GuestControllerDoc {

    // =======================================================
    // 🔹 FIND ALL
    // =======================================================
    @Operation(
            summary = "Find all Guests - Paginated",
            description = "Find all Guests with pagination support.",
            tags = { "Guests" },
            operationId = "findAllGuests",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Guests",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = GuestDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Guests found",
                            content = @Content
                    ),
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
    ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findAll(
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,

            @Parameter(description = "Sorting criteria: asc or desc", example = "asc")
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    // =======================================================
    // 🔹 FIND BY ID
    // =======================================================
    @Operation(
            summary = "Find Guest by ID",
            description = "Retrieve a single Guest by its unique ID.",
            tags = { "Guests" },
            operationId = "findGuestById",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns the Guest with the specified ID",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = GuestDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid ID supplied",
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
                            description = "Not Found - Guest with the specified ID does not exist",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = @Content
                    )
            }
    )
    GuestDTO findById(
            @Parameter(description = "ID of the guest to be retrieved", example = "1")
            @PathVariable("id") Long id
    );

    // =======================================================
    // 🔹 FIND BY NAME
    // =======================================================
    @Operation(
            summary = "Find Guests by Name - Paginated",
            description = "Find Guests by their name with pagination support.",
            tags = { "Guests" },
            operationId = "findGuestsByName",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Guests matching the name",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = GuestDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Guests found matching the provided name",
                            content = @Content
                    ),
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
    ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findByName(
            @Parameter(description = "Name of the guest to be retrieved", example = "John Doe")
            @PathVariable("name") String name,

            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,

            @Parameter(description = "Sorting criteria: asc or desc", example = "asc")
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    // =======================================================
    // 🔹 FIND BY EMAIL
    // =======================================================
    @Operation(
            summary = "Find Guests by Email - Paginated",
            description = "Find Guests by their email with pagination support.",
            tags = { "Guests" },
            operationId = "findGuestsByEmail",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Guests matching the email",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = GuestDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Guests found matching the provided email",
                            content = @Content
                    ),
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
    ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findGuestByEmail(
            @Parameter(description = "Email of the guest to be retrieved", example = "email@email.com")
            @PathVariable("email") String email,

            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,

            @Parameter(description = "Sorting criteria: asc or desc", example = "asc")
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    // =======================================================
    // 🔹 FIND BY PHONE
    // =======================================================
    @Operation(
            summary = "Find Guests by Phone - Paginated",
            description = "Find Guests by their phone with pagination support.",
            tags = { "Guests" },
            operationId = "findGuestsByPhone",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Guests matching the phone",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = GuestDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Guests found matching the phone",
                            content = @Content
                    ),
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
    ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findGuestByPhone(
            @Parameter(description = "Phone of the guest to be retrieved", example = "1234567890")
            @PathVariable("phone") String phone,

            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,

            @Parameter(description = "Sorting criteria: asc or desc", example = "asc")
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    // =======================================================
    // 🔹 EXPORT PAGE
    // =======================================================
    @Operation(
            summary = "Exports Guests in PDF, CSV, XLSX or XML format - Paginated",
            description = "Exports all Guests in a PDF, CSV, XLSX or XML file with pagination support.",
            tags = { "Guests" },
            operationId = "exportPageGuests",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Returns the file that contains the successfully exported guest data",
                            content = {
                                    @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XML_VALUE, schema = @Schema(type = "string", format = "binary"))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Guests found to export",
                            content = @Content
                    ),
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
    ResponseEntity<Resource> exportPage(
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,

            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,

            @Parameter(description = "Sorting criteria: asc or desc", example = "asc")
            @RequestParam(value = "direction", defaultValue = "asc") String direction,

            HttpServletRequest request
    );

    // =======================================================
    // 🔹 EXPORT GUEST
    // =======================================================
    @Operation(
            summary = "Exports Guest in PDF, CSV, XLSX or XML format - Paginated",
            description = "Exports one specific Guest in a PDF, CSV, XLSX or XML file with pagination support.",
            tags = { "Guests" },
            operationId = "exportGuest",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Returns the file that contains the successfully exported guest data",
                            content = {
                                    @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XML_VALUE, schema = @Schema(type = "string", format = "binary"))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Guests found to export",
                            content = @Content
                    ),
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
    ResponseEntity<Resource> exportGuest(
            @Parameter(description = "ID of the item", example = "1")
            @PathVariable("id") Long id,
            HttpServletRequest request
    );

    //TODO: massCreation
    @Operation(
            summary = "Mass Creation of Guests via File Upload",
            description = "Creates multiple Guests by uploading a CSV or XLSX file containing their data.",
            tags = { "Guests" },
            operationId = "massCreationGuests",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns the set of created Guests",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = GuestDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid file format or data",
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
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = @Content
                    )
            }
    )
    Set<GuestDTO> massCreation(
            @Parameter(description = "CSV or XLSX file containing people data", required = true)
            MultipartFile file
    );

    //TODO: create

    @Operation(
            summary = "Create a new Guest",
            description = "Creates a new Guest with the provided data.",
            tags = { "Guests" },
            operationId = "createGuest",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful operation - Returns the created Guest",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = GuestDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid file format or data",
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
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = @Content
                    )
            }
    )
    GuestDTO create(
            @Parameter(description = "Guest data to be created", required = true)
            @RequestBody GuestDTO guestDTO
    );
    //TODO: update
    @Operation(
            summary = "Update an existing Guest",
            description = "Updates an existing Guest with the provided data.",
            tags = { "Guests" },
            operationId = "updateGuest",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns the updated Guest",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = GuestDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid file format or data",
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
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = @Content
                    )
            }
    )
    GuestDTO update(
            @Parameter(description = "Guest data to be updated", required = true)
            @RequestBody GuestDTO guestDTO
    );

    @Operation(
            summary = "Delete a Guest by ID",
            description = "Deletes a Guest identified by its unique ID.",
            tags = { "Guests" },
            operationId = "deleteGuest",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful operation - Guest deleted successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid ID supplied",
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
                            description = "Not Found - Guest with the specified ID does not exist",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = @Content
                    )
            }
    )
    ResponseEntity<?> delete(
            @Parameter(description = "ID of the guest to be deleted", example = "1")
            @PathVariable("id") Long id
    );
}
