# Spring Core Concepts

## Introduction to Spring Framework

Spring Framework is a comprehensive programming and configuration model for modern Java-based enterprise applications. It provides infrastructure support so you can focus on your application logic.

## Key Principles

### 1. Inversion of Control (IoC)

IoC is a design principle where the control of object creation and dependency management is transferred from the application code to the framework.

**Traditional Approach:**
```java
public class OrderService {
    private PaymentService paymentService = new PaymentService(); // Tight coupling
    
    public void processOrder(Order order) {
        paymentService.processPayment(order.getAmount());
    }
}
```

**Spring IoC Approach:**
```java
@Service
public class OrderService {
    private PaymentService paymentService; // Dependency injected
    
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    public void processOrder(Order order) {
        paymentService.processPayment(order.getAmount());
    }
}
```

### 2. Dependency Injection (DI)

DI is a specific implementation of IoC where dependencies are provided to an object rather than the object creating them.

#### Types of Dependency Injection:

1. **Constructor Injection** (Recommended)
```java
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

2. **Setter Injection**
```java
@Service
public class UserService {
    private UserRepository userRepository;
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

3. **Field Injection** (Not recommended for production)
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
}
```

### 3. Spring Container (ApplicationContext)

The Spring container is responsible for managing the lifecycle of objects (beans) and their dependencies.

```java
@Configuration
public class AppConfig {
    
    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }
    
    @Bean
    public OrderService orderService(PaymentService paymentService) {
        return new OrderService(paymentService);
    }
}
```

## Bean Configuration Methods

### 1. Java-based Configuration (Recommended)

```java
@Configuration
@ComponentScan(basePackages = "com.example")
public class AppConfig {
    
    @Bean
    @Scope("singleton") // Default scope
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb");
        return new HikariDataSource(config);
    }
}
```

### 2. Annotation-based Configuration

```java
@Component
public class EmailService {
    
    @Value("${email.from}")
    private String fromEmail;
    
    public void sendEmail(String to, String subject, String body) {
        // Email sending logic
    }
}
```

### 3. XML Configuration (Legacy)

```xml
<beans xmlns="http://www.springframework.org/schema/beans">
    <bean id="paymentService" class="com.example.PaymentService"/>
    <bean id="orderService" class="com.example.OrderService">
        <constructor-arg ref="paymentService"/>
    </bean>
</beans>
```

## Bean Scopes

| Scope | Description |
|-------|-------------|
| **singleton** | One instance per Spring container (default) |
| **prototype** | New instance every time bean is requested |
| **request** | One instance per HTTP request (web apps) |
| **session** | One instance per HTTP session (web apps) |
| **application** | One instance per ServletContext (web apps) |
| **websocket** | One instance per WebSocket session |

## Common Annotations

### Core Annotations
- `@Component` - Generic stereotype for any Spring-managed component
- `@Service` - Specialization of @Component for service layer
- `@Repository` - Specialization of @Component for persistence layer
- `@Controller` - Specialization of @Component for presentation layer
- `@Configuration` - Indicates a class as a source of bean definitions

### Dependency Injection Annotations
- `@Autowired` - Enables automatic dependency injection
- `@Qualifier` - Specifies which bean to inject when multiple candidates exist
- `@Primary` - Gives preference to a bean when multiple candidates exist
- `@Value` - Injects values from properties files

### Example Usage
```java
@Service
public class NotificationService {
    
    @Autowired
    @Qualifier("emailNotifier")
    private NotificationProvider emailProvider;
    
    @Autowired
    @Qualifier("smsNotifier")
    private NotificationProvider smsProvider;
    
    @Value("${notification.default.type:email}")
    private String defaultType;
}
```

## Best Practices

1. **Prefer Constructor Injection**
   - Ensures mandatory dependencies
   - Promotes immutability
   - Easier to test

2. **Use @Primary and @Qualifier Wisely**
   - @Primary for default implementations
   - @Qualifier for specific requirements

3. **Keep Configuration Classes Focused**
   - Separate configurations by concern
   - Use @Import to combine configurations

4. **Leverage Profiles**
   ```java
   @Configuration
   @Profile("development")
   public class DevConfig {
       // Development-specific beans
   }
   ```

## Next Steps

1. Practice with the examples in `examples/01-hello-spring/`
2. Complete Assignment 1 to reinforce these concepts
3. Move on to Spring Boot basics in the next module

## Summary

Spring's core concepts revolve around IoC and DI, which promote loose coupling, testability, and maintainability. The Spring container manages bean lifecycles and dependencies, making your code more modular and easier to maintain.