package com.github.gomestkd.eventmanagement.dto.security;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class TokenDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private Boolean authenticated;
    private Instant created;
    private Instant expiration;
    private String accessToken;
    private String refreshToken;

    public TokenDTO() {
    }

    public TokenDTO(String username, Boolean authenticated, Instant created, Instant expiration, String accessToken, String refreshToken) {
        this.username = username;
        this.authenticated = authenticated;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TokenDTO tokenDTO)) return false;
        return Objects.equals(getUsername(), tokenDTO.getUsername()) && Objects.equals(getAuthenticated(), tokenDTO.getAuthenticated()) && Objects.equals(getCreated(), tokenDTO.getCreated()) && Objects.equals(getExpiration(), tokenDTO.getExpiration()) && Objects.equals(getAccessToken(), tokenDTO.getAccessToken()) && Objects.equals(getRefreshToken(), tokenDTO.getRefreshToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getAuthenticated(), getCreated(), getExpiration(), getAccessToken(), getRefreshToken());
    }
}
