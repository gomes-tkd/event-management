package com.github.gomestkd.eventmanagement.controllers.docs;

import com.github.gomestkd.eventmanagement.dto.EventDTO;
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

import java.util.Date;
import java.util.Set;

public interface EventControllerDocs {
    @Operation(
            summary = "FInd all Events - Paginated",
            description = "Find all Events with pagination support.",
            tags = { "Events" },
            operationId = "findAllEvents",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Events.",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Events Found",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication is required and gas failed or has not yet been provided",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - The request was valid, but the server is refusing action",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - The requested resource could not be found",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = { @Content }
                    )
            }

    )
    ResponseEntity<PagedModel<EntityModel<EventDTO>>> findAll(
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(
                    description = "Sorting criteria in the format: property (asc|desc). Default sort order is ascending. Multiple sort criteria are supported.",
                    example = "asc"
            )
            @RequestParam(value = "sort", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Find Event by ID",
            description = "Retrieve a single Event by its unique ID.",
            tags = { "Events" },
            operationId = "findEventById",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns the Gest with the specified ID",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = EventDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid ID supplied",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication is required and has failed or has not yet been provided",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - The request was valid, but the server is refusing action.",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Event with the specified ID does not exist.",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server.",
                            content = { @Content }
                    )
            }
    )
    EventDTO findById(
            @Parameter(description = "ID of the event to be retrived", example = "1")
            @PathVariable("id") Long id
    );

    @Operation(
            summary = "Find Events by Name - Paginated",
            description = "Find Events by their name with pagination support",
            tags = { "Events" },
            operationId = "findEventsByName",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Events matching the name.",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Events found matching the provided name.",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid parameters supplied",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Authentication is required and has failed or has not yet been provided",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - The request was valid, but the server is refusing action",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Fot Found - The requested resource could not be found",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - An error occurred on the server",
                            content = { @Content }
                    )
            }
    )
    ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByName(
            @Parameter(description = "Name of the event to be retrieved", example = "Sabaton Show - Band Rock show")
            @PathVariable("name")  String name,
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam("page") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(
                    description = "Sorting criteria in the format: property (asc|desc). Default sort order is ascending.",
                    example = "asc"
            )
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Find Events by Description - paginated",
            description = "Find Events by their description with pagination support.",
            tags = { "Events" },
            operationId = "findEventsByDescription",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Events matching the description",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Events found matching the provided location",
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
    ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByDescription(
            @Parameter(description = "Text used to search for matching descriptions events", example = "Rock")
            @PathVariable("description") String location,
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(
                    description = "Sorting criteria in the format: property (asc|desc). Default sort order is ascending.",
                    example = "asc"
            )
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Find Events by Location - paginated",
            description = "Finds Events by their location with pagination support.",
            tags = { "Events" },
            operationId = "findEventsByLocation",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list ofEvents matching the description",
                            content = {
                                @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                                )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Events found matching the provided location",
                            content = { @Content }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Invalid parameters supplied",
                            content = { @Content }
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
    ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByLocation(
            @Parameter(
                    description = "Text used to search for matching locations events",
                    example = "Calgary, Alberta - Canada"
            )
            @PathVariable("location") String location,
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam("page") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam("size") Integer size,
            @Parameter(
                    description = "Sorting criteria in the format: property (asc|desc). Default sort order is ascending",
                    example = "asc"
            )
            @RequestParam("direction") String direction
    );

    @Operation(
            summary = "Find Events by Time - Paginated",
            description = "Find Events by their time with pagination support.",
            tags = { "Events" },
            operationId = "findEventsByTime",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns a paginated list of Events matching the time",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Events found matching the time",
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
    ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByTime(
            @Parameter(description = "Start time of the event to be retrieved", example = "15/05/2025")
            @PathVariable("start-time") Date startTime,
            @Parameter(description = "Start time of the event to be retrieved", example = "15/05/2025")
            @PathVariable("end-time") Date endTime,
            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of records per page", example = "15")
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @Parameter(description = "Sorting criteria: asc or desc", example = "asc")
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @Operation(
            summary = "Exports Events in PDF, CSV, XLSX or XML format - Paginated",
            description = "Exports all Events in a PDF, CSV, XLSX or XML file with pagination support.",
            tags = { "Events" },
            operationId = "exportPageEvents",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Returns the file that contains the successfully exported Event data",
                            content = {
                                    @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XML_VALUE, schema = @Schema(type = "string", format = "binary"))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Events found to export",
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
    // 🔹 EXPORT Event
    // =======================================================
    @Operation(
            summary = "Exports Event in PDF, CSV, XLSX or XML format - Paginated",
            description = "Exports one specific Event in a PDF, CSV, XLSX or XML file with pagination support.",
            tags = { "Events" },
            operationId = "exportEvent",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful - Returns the file that contains the successfully exported Event data",
                            content = {
                                    @Content(mediaType = MediaTypes.APPLICATION_PDF_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_CSV_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XLSX_VALUE, schema = @Schema(type = "string", format = "binary")),
                                    @Content(mediaType = MediaTypes.APPLICATION_XML_VALUE, schema = @Schema(type = "string", format = "binary"))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content - No Events found to export",
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
    ResponseEntity<Resource> exportEvent(
            @Parameter(description = "ID of the item", example = "1")
            @PathVariable("id") Long id,
            HttpServletRequest request
    );

    //TODO: massCreation
    @Operation(
            summary = "Mass Creation of Events via File Upload",
            description = "Creates multiple Events by uploading a CSV or XLSX file containing their data.",
            tags = { "Events" },
            operationId = "massCreationEvents",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns the set of created Events",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(schema = @Schema(implementation = EventDTO.class))
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
    Set<EventDTO> massCreation(
            @Parameter(description = "CSV or XLSX file containing people data", required = true)
            MultipartFile file
    );

    //TODO: create

    @Operation(
            summary = "Create a new Event",
            description = "Creates a new Event with the provided data.",
            tags = { "Events" },
            operationId = "createEvent",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successful operation - Returns the created Event",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = EventDTO.class)
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
    EventDTO create(
            @Parameter(description = "Event data to be created", required = true)
            @RequestBody EventDTO EventDTO
    );
    //TODO: update
    @Operation(
            summary = "Update an existing Event",
            description = "Updates an existing Event with the provided data.",
            tags = { "Events" },
            operationId = "updateEvent",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation - Returns the updated Event",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = EventDTO.class)
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
    EventDTO update(
            @Parameter(description = "Event data to be updated", required = true)
            @RequestBody EventDTO EventDTO
    );

    @Operation(
            summary = "Delete a Event by ID",
            description = "Deletes a Event identified by its unique ID.",
            tags = { "Events" },
            operationId = "deleteEvent",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successful operation - Event deleted successfully",
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
                            description = "Not Found - Event with the specified ID does not exist",
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
            @Parameter(description = "ID of the Event to be deleted", example = "1")
            @PathVariable("id") Long id
    );
}
