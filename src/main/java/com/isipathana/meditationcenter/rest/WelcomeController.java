package com.isipathana.meditationcenter.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Welcome controller for root endpoint.
 * Provides API information and health status.
 *
 * @author Sathira Basnayake
 */
@RestController
public class WelcomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Isipathana International Meditation Center API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("timestamp", LocalDateTime.now());
        response.put("documentation", "/swagger-ui.html");
        response.put("health", "/actuator/health");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("auth", "/api/auth");
        endpoints.put("programs", "/api/programs");
        endpoints.put("events", "/api/events");
        response.put("endpoints", endpoints);

        return ResponseEntity.ok(response);
    }
}
