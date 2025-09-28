# Spring Data JPA

## Introduction

Spring Data JPA is part of the Spring Data family that makes it easy to implement JPA-based repositories. It eliminates boilerplate code and provides powerful features for data access.

## Key Benefits

- **Reduced Boilerplate**: No need to write basic CRUD operations
- **Query Methods**: Create queries from method names
- **Custom Queries**: Support for JPQL and native SQL
- **Pagination and Sorting**: Built-in support
- **Auditing**: Automatic tracking of creation/modification times
- **Transaction Management**: Declarative transaction support

## Core Concepts

### 1. Entity Classes

```java
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {}
    
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    // Getters and Setters
    // ... (omitted for brevity)
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 2. Repository Interfaces

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Query methods derived from method names
    Optional<User> findByEmail(String email);
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    List<User> findByLastNameOrderByFirstNameAsc(String lastName);
    
    // Custom JPQL queries
    @Query("SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName")
    List<User> findByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain%")
    List<User> findByEmailDomain(@Param("domain") String domain);
    
    // Native SQL queries
    @Query(value = "SELECT * FROM users WHERE created_at > ?1", nativeQuery = true)
    List<User> findUsersCreatedAfter(LocalDateTime date);
    
    // Count queries
    long countByFirstName(String firstName);
    
    // Exists queries
    boolean existsByEmail(String email);
    
    // Delete queries
    void deleteByEmail(String email);
}
```

## Repository Types

### 1. CrudRepository
Basic CRUD operations:
```java
public interface UserRepository extends CrudRepository<User, Long> {
    // Provides: save, findById, findAll, count, delete, etc.
}
```

### 2. PagingAndSortingRepository
Adds pagination and sorting:
```java
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    // Includes CrudRepository methods plus:
    // findAll(Sort), findAll(Pageable)
}
```

### 3. JpaRepository
Most commonly used, includes all previous features:
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Includes all previous methods plus:
    // flush(), saveAndFlush(), deleteInBatch(), etc.
}
```

## Query Methods

### Method Name Patterns

| Keyword | Sample | JPQL |
|---------|---------|------|
| `And` | `findByNameAndEmail` | `... where x.name = ?1 and x.email = ?2` |
| `Or` | `findByNameOrEmail` | `... where x.name = ?1 or x.email = ?2` |
| `Is`, `Equals` | `findByName` | `... where x.name = ?1` |
| `Between` | `findByAgeBetween` | `... where x.age between ?1 and ?2` |
| `LessThan` | `findByAgeLessThan` | `... where x.age < ?1` |
| `GreaterThan` | `findByAgeGreaterThan` | `... where x.age > ?1` |
| `Like` | `findByNameLike` | `... where x.name like ?1` |
| `IgnoreCase` | `findByNameIgnoreCase` | `... where UPPER(x.name) = UPPER(?1)` |
| `OrderBy` | `findByAgeOrderByNameDesc` | `... order by x.name desc` |
| `Not` | `findByNameNot` | `... where x.name <> ?1` |
| `In` | `findByAgeIn` | `... where x.age in ?1` |
| `NotIn` | `findByAgeNotIn` | `... where x.age not in ?1` |
| `True` | `findByActiveTrue` | `... where x.active = true` |
| `False` | `findByActiveFalse` | `... where x.active = false` |

### Examples

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find users by age range
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // Find users with names starting with prefix
    List<User> findByFirstNameStartingWith(String prefix);
    
    // Find users created after a date, ordered by name
    List<User> findByCreatedAtAfterOrderByFirstNameAsc(LocalDateTime date);
    
    // Find users by multiple criteria
    List<User> findByFirstNameAndLastNameAndAgeGreaterThan(
        String firstName, String lastName, Integer age);
    
    // Case-insensitive search
    List<User> findByEmailContainingIgnoreCase(String email);
}
```

## Pagination and Sorting

### Basic Pagination

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }
    
    public Page<User> getUsersSorted(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }
}
```

### Controller with Pagination

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Page<User> users = userService.getUsersSorted(page, size, sortBy, sortDir);
        return ResponseEntity.ok(users);
    }
}
```

## Relationships

### One-to-Many Relationship

```java
// Parent Entity
@Entity
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
    
    // Getters and setters
}

// Child Entity
@Entity
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Getters and setters
}
```

### Many-to-Many Relationship

```java
@Entity
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
    
    // Getters and setters
}

@Entity
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
    
    // Getters and setters
}
```

## Transactions

### Declarative Transactions

```java
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Method is transactional by default (from class level)
    public User createUser(User user) {
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser.getEmail());
        return savedUser;
    }
    
    // Read-only transaction for better performance
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Custom transaction settings
    @Transactional(
        isolation = Isolation.READ_COMMITTED,
        propagation = Propagation.REQUIRED,
        timeout = 30,
        rollbackFor = Exception.class
    )
    public void complexBusinessOperation() {
        // Complex operation that might fail
    }
}
```

## Custom Repository Implementation

### Creating Custom Repository

```java
// Custom interface
public interface UserRepositoryCustom {
    List<User> findUsersWithCustomLogic(String criteria);
}

// Implementation
@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<User> findUsersWithCustomLogic(String criteria) {
        String jpql = "SELECT u FROM User u WHERE u.firstName LIKE :criteria OR u.lastName LIKE :criteria";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("criteria", "%" + criteria + "%");
        return query.getResultList();
    }
}

// Main repository extending custom interface
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    // Standard repository methods + custom methods
}
```

## Configuration

### Database Configuration (application.properties)

```properties
# H2 Database (for development)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# MySQL Configuration
# spring.datasource.url=jdbc:mysql://localhost:3306/mydb
# spring.datasource.username=root
# spring.datasource.password=password
# spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect

# Connection Pool Configuration
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.maximum-pool-size=20
```

### Java Configuration

```java
@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository")
@EnableTransactionManagement
public class JpaConfig {
    
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.model");
        
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        return em;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }
}
```

## Testing

### Repository Testing

```java
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User("John", "Doe", "john.doe@example.com");
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }
    
    @Test
    void shouldFindUsersByFirstNameContaining() {
        // Given
        entityManager.persistAndFlush(new User("John", "Doe", "john@example.com"));
        entityManager.persistAndFlush(new User("Johnny", "Smith", "johnny@example.com"));
        entityManager.persistAndFlush(new User("Jane", "Doe", "jane@example.com"));
        
        // When
        List<User> users = userRepository.findByFirstNameContainingIgnoreCase("john");
        
        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getFirstName).containsExactlyInAnyOrder("John", "Johnny");
    }
}
```

## Best Practices

### 1. Use DTOs for API Responses
```java
@Service
public class UserService {
    
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        return convertToDTO(user);
    }
    
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .build();
    }
}
```

### 2. Use Projections for Limited Data
```java
// Interface-based projection
public interface UserSummary {
    String getFirstName();
    String getLastName();
    String getEmail();
}

public interface UserRepository extends JpaRepository<User, Long> {
    List<UserSummary> findAllProjectedBy();
}

// Class-based projection
public class UserInfo {
    private String firstName;
    private String lastName;
    
    public UserInfo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters
}

@Query("SELECT new com.example.dto.UserInfo(u.firstName, u.lastName) FROM User u")
List<UserInfo> findAllUserInfo();
```

### 3. Handle N+1 Queries
```java
// Problem: N+1 queries
List<Department> departments = departmentRepository.findAll();
for (Department dept : departments) {
    dept.getEmployees().size(); // This triggers N additional queries
}

// Solution: Use JOIN FETCH
@Query("SELECT d FROM Department d JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

### 4. Use Specifications for Dynamic Queries
```java
public class UserSpecifications {
    
    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> 
            firstName == null ? null : criteriaBuilder.equal(root.get("firstName"), firstName);
    }
    
    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> 
            email == null ? null : criteriaBuilder.like(root.get("email"), "%" + email + "%");
    }
}

// Usage
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {}

List<User> users = userRepository.findAll(
    Specification.where(UserSpecifications.hasFirstName("John"))
        .and(UserSpecifications.hasEmail("example.com"))
);
```

## Next Steps

1. Practice with the examples in `examples/03-spring-data-jpa-demo/`
2. Complete Assignment 3 on Spring Data JPA
3. Learn about Spring Security in the next module

## Summary

Spring Data JPA simplifies data access by:
- Eliminating boilerplate repository code
- Providing powerful query methods
- Supporting pagination and sorting
- Handling transactions declaratively
- Offering flexible relationship mapping