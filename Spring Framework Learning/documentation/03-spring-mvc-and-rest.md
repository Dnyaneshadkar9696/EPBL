# Spring MVC and REST APIs

## Introduction to Spring MVC

Spring MVC (Model-View-Controller) is a web framework built on the Servlet API and is part of the Spring Framework. It follows the MVC design pattern and provides a flexible way to build web applications and REST APIs.

## MVC Architecture

```
Request → DispatcherServlet → Controller → Service → Repository → Database
    ↑                            ↓
Response ← View ← Model ← Controller
```

### Components:

1. **Model**: Data and business logic
2. **View**: Presentation layer (JSP, Thymeleaf, or JSON for REST)
3. **Controller**: Handles requests and coordinates between model and view

## Core Annotations

### Controller Annotations

```java
@Controller        // Traditional MVC controller (returns view names)
@RestController    // REST controller (returns data directly, combines @Controller + @ResponseBody)
@RequestMapping    // Maps HTTP requests to handler methods
@GetMapping        // HTTP GET requests
@PostMapping       // HTTP POST requests
@PutMapping        // HTTP PUT requests
@DeleteMapping     // HTTP DELETE requests
@PatchMapping      // HTTP PATCH requests
```

### Parameter Annotations

```java
@PathVariable      // Extract values from URI path
@RequestParam      // Extract query parameters
@RequestBody       // Bind HTTP request body to method parameter
@RequestHeader     // Extract HTTP headers
@CookieValue      // Extract cookie values
```

## Building REST Controllers

### Basic REST Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    // Constructor injection (recommended)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
    
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }
    
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.save(product);
    }
    
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productService.update(product);
    }
    
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
```

### Advanced Parameter Handling

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    // Multiple path variables
    @GetMapping("/department/{deptId}/user/{userId}")
    public User getUserFromDepartment(
            @PathVariable Long deptId,
            @PathVariable Long userId) {
        return userService.findByDepartmentAndUserId(deptId, userId);
    }
    
    // Query parameters with defaults
    @GetMapping("/search")
    public List<User> searchUsers(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return userService.search(name, page, size);
    }
    
    // Request headers
    @GetMapping("/profile")
    public User getUserProfile(@RequestHeader("Authorization") String token) {
        return userService.findByToken(token);
    }
    
    // Complex request body
    @PostMapping("/batch")
    public List<User> createUsers(@RequestBody List<User> users) {
        return userService.saveAll(users);
    }
}
```

## HTTP Status Codes

### Using ResponseEntity for Custom Status Codes

```java
@RestController
public class ResponseEntityController {
    
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(user);  // 200 OK
        } else {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
    }
    
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201 Created
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
```

### Common HTTP Status Codes

| Code | Status | Usage |
|------|--------|-------|
| 200 | OK | Successful GET, PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Access denied |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Resource conflict |
| 500 | Internal Server Error | Server error |

## Request/Response Processing

### Content Negotiation

```java
@GetMapping(value = "/users/{id}", produces = {
    MediaType.APPLICATION_JSON_VALUE,
    MediaType.APPLICATION_XML_VALUE
})
public User getUser(@PathVariable Long id) {
    return userService.findById(id);
}

@PostMapping(value = "/users", 
             consumes = MediaType.APPLICATION_JSON_VALUE,
             produces = MediaType.APPLICATION_JSON_VALUE)
public User createUser(@RequestBody User user) {
    return userService.save(user);
}
```

### Request Validation

```java
import javax.validation.Valid;
import javax.validation.constraints.*;

// Entity with validation annotations
public class User {
    @NotNull(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 65, message = "Age must be less than 65")
    private Integer age;
}

// Controller using validation
@PostMapping("/users")
public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    User created = userService.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```

## Exception Handling

### Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", "Invalid input", errors);
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// Error response model
public class ErrorResponse {
    private String code;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;
    
    // constructors, getters, setters
}
```

### Custom Exceptions

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
```

## CORS Configuration

### Method-level CORS

```java
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    
    @CrossOrigin(origins = {"http://localhost:3000", "https://example.com"})
    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.findAll();
    }
}
```

### Global CORS Configuration

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "https://example.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

## Best Practices

### 1. RESTful URL Design
```
Good:
GET    /api/users          - Get all users
GET    /api/users/123      - Get specific user
POST   /api/users          - Create new user
PUT    /api/users/123      - Update user
DELETE /api/users/123      - Delete user

Avoid:
GET    /api/getUsers
POST   /api/createUser
GET    /api/user/delete/123
```

### 2. Use DTOs (Data Transfer Objects)
```java
// Instead of exposing entity directly
@GetMapping("/users/{id}")
public UserDTO getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return userMapper.toDTO(user);
}
```

### 3. Implement Proper Error Responses
```java
{
  "timestamp": "2023-10-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User with id 123 not found",
  "path": "/api/users/123"
}
```

### 4. Use HTTP Methods Correctly
- **GET**: Retrieve data (idempotent)
- **POST**: Create new resources
- **PUT**: Update entire resource (idempotent)
- **PATCH**: Partial update
- **DELETE**: Remove resource (idempotent)

### 5. Implement Pagination
```java
@GetMapping("/users")
public Page<User> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return userService.findAll(pageable);
}
```

## Testing REST Controllers

### Unit Testing with MockMvc

```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Test
    void shouldReturnUser_WhenValidId() throws Exception {
        User user = new User(1L, "John Doe", "john@example.com", 25);
        when(userService.findById(1L)).thenReturn(user);
        
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }
    
    @Test
    void shouldCreateUser_WhenValidInput() throws Exception {
        User user = new User(null, "Jane Doe", "jane@example.com", 30);
        User savedUser = new User(1L, "Jane Doe", "jane@example.com", 30);
        
        when(userService.save(any(User.class))).thenReturn(savedUser);
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Jane Doe\",\"email\":\"jane@example.com\",\"age\":30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }
}
```

## Summary

Spring MVC provides a powerful framework for building web applications and REST APIs. Key points:

- Use `@RestController` for REST APIs
- Proper HTTP methods and status codes
- Implement comprehensive error handling
- Follow RESTful design principles
- Use validation for data integrity
- Test your controllers thoroughly

## Next Steps

1. Practice with the examples in `examples/02-rest-api-demo/`
2. Complete Assignment 2 on REST API development
3. Learn about Spring Data JPA in the next module