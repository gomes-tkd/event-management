package com.github.gomestkd.eventmanagement.controllers.docs;

import com.github.gomestkd.eventmanagement.dto.security.AccountCredentialsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface AuthControllerDoc {
    @Operation(
        summary = "Authenticates a user and returns a token",
        description = "Validates user credentials and generates an access token for authentication.",
        tags = {"Authentication"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials);

    @Operation(
        summary = "Refresh token for authenticated user and returns a token",
        description = "Generates a new access token using the provided refresh token and username.",
        tags = {"Authentication"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    ResponseEntity<?> refreshToken(
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String refreshToken
    );

    @Operation(
        summary = "Create a new User",
        description = "Registers a new user in the system with the provided credentials.",
        tags = {"Authentication"},
        responses = {
            @ApiResponse(description = "Created", responseCode = "201", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Conflict", responseCode = "409", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials);
}
