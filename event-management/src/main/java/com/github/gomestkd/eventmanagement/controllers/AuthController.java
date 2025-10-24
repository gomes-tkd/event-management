package com.github.gomestkd.eventmanagement.controllers;

import com.github.gomestkd.eventmanagement.controllers.docs.AuthControllerDoc;
import com.github.gomestkd.eventmanagement.dto.security.AccountCredentialsDTO;
import com.github.gomestkd.eventmanagement.dto.security.TokenDTO;
import com.github.gomestkd.eventmanagement.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController implements AuthControllerDoc {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
        value = { "/signin" },
        produces = { MediaType.APPLICATION_JSON_VALUE },
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials) {
        if (credentialsIsInvalid(credentials)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Credentials are invalid");
        }

        ResponseEntity<TokenDTO> token = authService.signIn(credentials);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sign in: Invalid username or password");
        }

        return token;
    }

    @PutMapping(
        value = { "/refresh/{username}" },
        produces = { MediaType.APPLICATION_JSON_VALUE },
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ResponseEntity<?> refreshToken(
        @PathVariable("username") String username,
        @RequestHeader("Authorization") String refreshToken
    ) {
        if (parametersAreInvalid(username, refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh: Invalid client request");
        }

        ResponseEntity<TokenDTO> token = authService.refreshToken(username, refreshToken);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Refresh: Invalid username or refresh token");
        }

        return token;
    }

    @PostMapping(
        value = { "/createUser" },
        produces = { MediaType.APPLICATION_JSON_VALUE },
        consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO credentials) {
        return authService.create(credentials);
    }

    private boolean parametersAreInvalid(String username, String refreshToken) {
        return StringUtils.isBlank(username) || StringUtils.isBlank(refreshToken);
    }

    private static boolean credentialsIsInvalid(AccountCredentialsDTO credentials) {
        return credentials == null ||
                StringUtils.isBlank(credentials.getPassword()) ||
                StringUtils.isBlank(credentials.getUsername());
    }
}
