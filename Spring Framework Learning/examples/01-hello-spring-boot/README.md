# Hello Spring Boot Example

This is a simple Spring Boot application that demonstrates basic concepts and features.

## What You'll Learn

- Creating a Spring Boot application
- Using `@SpringBootApplication` annotation
- Building REST controllers with `@RestController`
- Handling different HTTP methods (GET, POST)
- Using path variables and query parameters
- Injecting configuration properties with `@Value`
- Working with Spring Boot Actuator

## Project Structure

```
01-hello-spring-boot/
├── pom.xml                          # Maven configuration
├── src/main/java/com/example/hello/
│   ├── HelloSpringBootApplication.java    # Main application class
│   └── controller/
│       └── HelloController.java           # REST controller
└── src/main/resources/
    └── application.properties              # Application configuration
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+ or IDE with Maven support

## Running the Application

### Option 1: Using Maven

```bash
# Navigate to the example directory
cd "Spring Framework Learning/examples/01-hello-spring-boot"

# Run the application
mvn spring-boot:run
```

### Option 2: Using IDE

1. Import the project as a Maven project
2. Run the `HelloSpringBootApplication.main()` method

### Option 3: Package and Run JAR

```bash
# Package the application
mvn clean package

# Run the JAR file
java -jar target/hello-spring-boot-1.0.0.jar
```

## Testing the Endpoints

Once the application is running, you can test the following endpoints:

### Basic Endpoints

```bash
# Welcome message
curl http://localhost:8080/api/

# Personalized greeting
curl http://localhost:8080/api/hello/John

# Custom greeting with query parameters
curl "http://localhost:8080/api/greet?name=Alice&greeting=Hi"

# Application information
curl http://localhost:8080/api/info
```

### POST Endpoint

```bash
# Create user (POST with JSON)
curl -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

## Key Features Demonstrated

### 1. Auto-Configuration
- Spring Boot automatically configures the embedded Tomcat server
- JSON message conversion is handled automatically
- No XML configuration needed

### 2. Dependency Injection
- `@Value` annotation injects properties from `application.properties`
- Spring manages the controller as a bean

### 3. REST API Development
- `@RestController` combines `@Controller` and `@ResponseBody`
- Different HTTP methods with appropriate annotations
- Automatic JSON serialization/deserialization

### 4. Configuration Management
- External configuration through `application.properties`
- Default values with `@Value("${property:defaultValue}")`

### 5. Monitoring with Actuator
- Health checks for application monitoring
- Metrics and application information endpoints

## Exercises

1. **Add a new endpoint** that returns the current server time
2. **Create a configuration class** using `@ConfigurationProperties`
3. **Add input validation** to the POST endpoint
4. **Create a custom health indicator** for Actuator
5. **Add logging** to see when endpoints are called

## Expected Output

When you run the application, you should see:

```
🚀 Hello Spring Boot Application Started!
📍 Access the application at: http://localhost:8080
🔍 Health check at: http://localhost:8080/actuator/health
```

And when you visit `http://localhost:8080/api/`:

```
🌟 Welcome to Spring Boot Learning! (Version: 1.0.0)
```

## Next Steps

1. Try modifying the controller to add new endpoints
2. Experiment with different HTTP methods
3. Add more configuration properties
4. Move on to the next example: Spring Data JPA

## Troubleshooting

**Port 8080 already in use?**
- Change the port in `application.properties`: `server.port=8081`

**Application not starting?**
- Check Java version: `java -version`
- Verify Maven installation: `mvn -version`

**404 errors?**
- Ensure you're using the correct URL paths
- Check the application logs for any errors