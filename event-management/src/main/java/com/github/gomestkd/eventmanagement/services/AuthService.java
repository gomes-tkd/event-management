package com.github.gomestkd.eventmanagement.services;

import com.github.gomestkd.eventmanagement.dto.security.AccountCredentialsDTO;
import com.github.gomestkd.eventmanagement.dto.security.TokenDTO;
import com.github.gomestkd.eventmanagement.exception.RequiredObjectIsNullException;
import com.github.gomestkd.eventmanagement.model.User;
import com.github.gomestkd.eventmanagement.repositories.UserRepository;
import com.github.gomestkd.eventmanagement.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials) {
        logger.info("Attempting to authenticate user: {}", credentials.getUsername());

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword())
        );

        User user = userRepository.findByUsername(credentials.getUsername());

        if (user == null) {
            logger.error("User not found: {}", credentials.getUsername());
            throw new UsernameNotFoundException("Username " + credentials.getUsername() + " not found!");
        }

        List<String> roles = new ArrayList<>(user.getRoles());
        String username = user.getUsername();

        TokenDTO token = jwtTokenProvider.createAccessToken(username, roles);
        logger.info("User successfully authenticated: {}", username);

        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenDTO> refreshToken(String username, String refreshToken) {
        logger.info("Token refresh requested for user: {}", username);

        User user = userRepository.findByUsername(username);

        TokenDTO token = null;

        if (user != null) {
            logger.debug("User found: {}, refreshing token...", username);
            token = jwtTokenProvider.refreshToken(refreshToken);
        } else {
            logger.error("Failed to refresh token. User not found: {}", username);
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }

        logger.info("Token successfully refreshed for user: {}", username);
        return ResponseEntity.ok(token);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO user) {
        logger.info("Creating new user: {}", user != null ? user.getUsername() : "NULL");

        if (user == null) {
            logger.error("Attempted to create a null user.");
            throw new RequiredObjectIsNullException();
        }

        User entity = new User();

        entity.setFullName(user.getFullName());
        entity.setUsername(user.getUsername());
        entity.setPassword(generateHashedPassword(user.getPassword()));
        entity.setEmail(user.getEmail());
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        User dto = userRepository.save(entity);
        logger.info("User successfully created: {}", dto.getUsername());

        return new AccountCredentialsDTO(
            dto.getUsername(),
            dto.getPassword(),
            dto.getFullName(),
            dto.getEmail()
        );
    }

    private String generateHashedPassword(String password) {
        logger.debug("Generating password hash using PBKDF2.");

        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder(
                "", 8, 185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        );

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);

        passwordEncoder.setDefaultPasswordEncoderForMatches(passwordEncoder);

        logger.debug("Password successfully hashed.");
        return passwordEncoder.encode(password);
    }
}
