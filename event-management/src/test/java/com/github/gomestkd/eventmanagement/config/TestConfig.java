package com.github.gomestkd.eventmanagement.config;

public interface TestConfig {
    int SERVER_PORT = 8888;

    String HEADER_PARAM_AUTHORIZATION = "Authorization";
    String HEADER_PARAM_ORIGIN = "Origin";
    String ORIGIN_LOCAL = "http://localhost:8080";
    String ORIGINAL_LOCAL_2 = "http://localhost:3000";
    String ORIGIN_NOT_ALLOWED = "https://store.steampowered.com/";
}
