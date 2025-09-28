package com.example.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class
 * 
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Enables component scanning in the package and sub-packages
 */
@SpringBootApplication
public class HelloSpringBootApplication {

    public static void main(String[] args) {
        // SpringApplication.run() starts the Spring application context
        // and embedded web server (Tomcat by default)
        SpringApplication.run(HelloSpringBootApplication.class, args);
        
        System.out.println("🚀 Hello Spring Boot Application Started!");
        System.out.println("📍 Access the application at: http://localhost:8080");
        System.out.println("🔍 Health check at: http://localhost:8080/actuator/health");
    }
}