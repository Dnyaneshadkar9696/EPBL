# Spring Boot Basics

## What is Spring Boot?

Spring Boot is an opinionated framework built on top of Spring Framework that simplifies the development of Spring applications by providing:

- **Auto-configuration**: Automatically configures Spring applications based on dependencies
- **Starter Dependencies**: Pre-configured dependency bundles
- **Embedded Servers**: Built-in Tomcat, Jetty, or Undertow
- **Production-ready Features**: Health checks, metrics, externalized configuration

## Key Features

### 1. Auto-Configuration

Spring Boot automatically configures your application based on the dependencies you've added.

```java
@SpringBootApplication // Combines @Configuration, @EnableAutoConfiguration, @ComponentScan
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### 2. Starter Dependencies

Starters are dependency descriptors that bring in all related dependencies.

**Common Starters:**
- `spring-boot-starter-web` - Web applications with Spring MVC
- `spring-boot-starter-data-jpa` - JPA with Hibernate
- `spring-boot-starter-security` - Security features
- `spring-boot-starter-test` - Testing framework
- `spring-boot-starter-actuator` - Production-ready features

**Example Maven dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 3. Embedded Server

No need for external server deployment:

```java
@SpringBootApplication
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        // Application runs on embedded Tomcat (default port 8080)
    }
}
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/myapp/
│   │       ├── MyApplication.java
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       └── model/
│   └── resources/
│       ├── application.properties
│       ├── static/          # CSS, JS, images
│       └── templates/       # Thymeleaf templates
└── test/
    └── java/
        └── com/example/myapp/
```

## Configuration

### application.properties
```properties
# Server configuration
server.port=8081
server.servlet.context-path=/api

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# Logging configuration
logging.level.com.example=DEBUG
logging.file.name=myapp.log
```

### application.yml (Alternative)
```yaml
server:
  port: 8081
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: user
    password: password
  jpa:
    hibernate:
      ddl-auto: update

logging:
  level:
    com.example: DEBUG
  file:
    name: myapp.log
```

## Profiles

Use profiles for different environments:

**application-dev.properties:**
```properties
spring.datasource.url=jdbc:h2:mem:devdb
logging.level.root=DEBUG
```

**application-prod.properties:**
```properties
spring.datasource.url=jdbc:mysql://prod-server:3306/proddb
logging.level.root=WARN
```

**Activating profiles:**
```java
// In code
SpringApplication app = new SpringApplication(MyApplication.class);
app.setAdditionalProfiles("dev");
app.run(args);

// Command line
java -jar myapp.jar --spring.profiles.active=prod

// Environment variable
export SPRING_PROFILES_ACTIVE=dev
```

## Creating Your First Spring Boot Application

### 1. Using Spring Initializr

Visit [start.spring.io](https://start.spring.io) and select:
- Project: Maven Project
- Language: Java
- Spring Boot: 2.7.x or 3.x
- Dependencies: Spring Web

### 2. Manual Setup

**pom.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.5</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>spring-boot-demo</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 3. Main Application Class

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 4. Simple REST Controller

```java
package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    
    @GetMapping("/")
    public String home() {
        return "Hello, Spring Boot!";
    }
    
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return String.format("Hello, %s!", name);
    }
}
```

## Running the Application

### Maven Commands
```bash
# Run the application
./mvnw spring-boot:run

# Package as JAR
./mvnw clean package

# Run the packaged JAR
java -jar target/demo-1.0.0.jar
```

### Gradle Commands
```bash
# Run the application
./gradlew bootRun

# Package as JAR
./gradlew build

# Run the packaged JAR
java -jar build/libs/demo-1.0.0.jar
```

## Spring Boot Actuator

Add monitoring and management features:

**Dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**Configuration:**
```properties
# Expose all actuator endpoints
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
```

**Available endpoints:**
- `/actuator/health` - Application health
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/env` - Environment properties

## DevTools for Development

Add for automatic restart and live reload:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Features:
- Automatic restart when classpath changes
- Live reload for browser
- Disable caching for development

## Best Practices

1. **Use Constructor Injection**
2. **Externalize Configuration**
3. **Use Profiles for Different Environments**
4. **Keep Main Class Simple**
5. **Use Starter Dependencies**
6. **Enable Actuator for Production Monitoring**

## Common Annotations

- `@SpringBootApplication` - Main application class
- `@RestController` - REST API controller
- `@Service` - Service layer component
- `@Repository` - Data access layer component
- `@Component` - Generic Spring component
- `@Configuration` - Configuration class
- `@ConfigurationProperties` - Bind properties to objects

## Next Steps

1. Explore the example in `examples/02-spring-boot-hello/`
2. Complete Assignment 2 on Spring Boot basics
3. Learn about Spring MVC in the next module

## Summary

Spring Boot simplifies Spring application development through auto-configuration, starter dependencies, and embedded servers. It follows the "convention over configuration" principle, allowing developers to focus on business logic rather than boilerplate configuration.