package com.example.hello.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller demonstrating basic Spring Boot web features
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    // Injecting property values from application.properties
    @Value("${app.welcome.message:Welcome to Spring Boot!}")
    private String welcomeMessage;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    /**
     * Simple GET endpoint returning a string
     */
    @GetMapping("/")
    public String home() {
        return "🌟 " + welcomeMessage + " (Version: " + appVersion + ")";
    }

    /**
     * GET endpoint with path variable
     */
    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return String.format("👋 Hello, %s! Current time: %s", 
                             name, LocalDateTime.now());
    }

    /**
     * GET endpoint with query parameters
     */
    @GetMapping("/greet")
    public String greet(@RequestParam(defaultValue = "World") String name,
                       @RequestParam(defaultValue = "Hello") String greeting) {
        return String.format("%s, %s! 🎉", greeting, name);
    }

    /**
     * POST endpoint accepting JSON data
     */
    @PostMapping("/user")
    public Map<String, Object> createUser(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User created successfully!");
        response.put("user", user);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "success");
        return response;
    }

    /**
     * GET endpoint returning JSON object
     */
    @GetMapping("/info")
    public Map<String, Object> getApplicationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("applicationName", "Hello Spring Boot");
        info.put("version", appVersion);
        info.put("description", "A simple Spring Boot REST API demonstration");
        info.put("timestamp", LocalDateTime.now());
        info.put("endpoints", new String[]{
            "GET /api/ - Welcome message",
            "GET /api/hello/{name} - Personalized greeting",
            "GET /api/greet?name=X&greeting=Y - Customizable greeting",
            "POST /api/user - Create user (send JSON body)",
            "GET /api/info - Application information"
        });
        return info;
    }

    /**
     * Exception handling example
     */
    @GetMapping("/error-demo")
    public String errorDemo(@RequestParam(required = false) String cause) {
        if ("npe".equals(cause)) {
            throw new NullPointerException("This is a demo null pointer exception");
        } else if ("illegal".equals(cause)) {
            throw new IllegalArgumentException("This is a demo illegal argument exception");
        }
        return "No error occurred. Try: ?cause=npe or ?cause=illegal";
    }
}