# Testing Spring Applications

## Introduction

Testing is a crucial part of Spring application development. Spring provides excellent testing support with dedicated annotations, test slices, and integration capabilities.

## Types of Tests

### 1. Unit Tests
Test individual components in isolation:
- Service methods
- Utility classes
- Domain logic

### 2. Integration Tests
Test components working together:
- Repository with database
- Web layer with controllers
- Full application context

### 3. Test Slices
Spring Boot provides specialized test annotations:
- `@WebMvcTest` - Web layer only
- `@DataJpaTest` - JPA repositories only
- `@JsonTest` - JSON serialization
- `@TestRestTemplate` - REST clients

## Core Testing Annotations

### @SpringBootTest
Loads the complete application context:

```java
@SpringBootTest
class ApplicationIntegrationTest {
    
    @Autowired
    private BookService bookService;
    
    @Test
    void contextLoads() {
        assertThat(bookService).isNotNull();
    }
}
```

### @WebMvcTest
Tests only the web layer:

```java
@WebMvcTest(BookController.class)
class BookControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
    
    @Test
    void shouldReturnBooks() throws Exception {
        List<Book> books = Arrays.asList(new Book("Title", "ISBN"));
        when(bookService.getAllBooks()).thenReturn(books);
        
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Title")));
    }
}
```

### @DataJpaTest
Tests JPA repositories:

```java
@DataJpaTest
class BookRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Test
    void shouldFindBookByTitle() {
        // Given
        Book book = new Book("Test Title", "123456789");
        entityManager.persistAndFlush(book);
        
        // When
        List<Book> found = bookRepository.findByTitleContainingIgnoreCase("test");
        
        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Test Title");
    }
}
```

## Testing Components

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
        User user = new User("John", "Doe", "john@example.com");
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }
    
    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldCountUsersByStatus() {
        // Given
        entityManager.persistAndFlush(new User("Active", "User", "active@example.com", true));
        entityManager.persistAndFlush(new User("Inactive", "User", "inactive@example.com", false));
        
        // When
        long activeCount = userRepository.countByActiveTrue();
        
        // Then
        assertThat(activeCount).isEqualTo(1);
    }
}
```

### Service Testing

```java
@SpringBootTest
class BookServiceTest {
    
    @Autowired
    private BookService bookService;
    
    @MockBean
    private BookRepository bookRepository;
    
    @MockBean
    private AuthorRepository authorRepository;
    
    @Test
    void shouldCreateBookSuccessfully() {
        // Given
        Author author = new Author("Test Author", "test@example.com");
        Book book = new Book("New Book", "978-1234567890", BigDecimal.valueOf(19.99), author);
        
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        // When
        Book created = bookService.createBook(book);
        
        // Then
        assertThat(created.getTitle()).isEqualTo("New Book");
        verify(bookRepository).save(book);
    }
    
    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book not found");
    }
}
```

### Controller Testing

```java
@WebMvcTest(BookController.class)
class BookControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
    
    @Test
    void shouldReturnAllBooks() throws Exception {
        // Given
        List<Book> books = Arrays.asList(
            new Book("Book 1", "ISBN1", BigDecimal.valueOf(10.99)),
            new Book("Book 2", "ISBN2", BigDecimal.valueOf(15.99))
        );
        when(bookService.getAllBooks()).thenReturn(books);
        
        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpected(jsonPath("$[0].title", is("Book 1")))
                .andExpect(jsonPath("$[1].price", is(15.99)));
    }
    
    @Test
    void shouldCreateBookSuccessfully() throws Exception {
        // Given
        Book book = new Book("New Book", "978-1234567890", BigDecimal.valueOf(19.99));
        when(bookService.createBook(any(Book.class))).thenReturn(book);
        
        // When & Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "title": "New Book",
                        "isbn": "978-1234567890",
                        "price": 19.99
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.isbn", is("978-1234567890")));
    }
    
    @Test
    void shouldReturnBadRequestForInvalidData() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        when(bookService.getBookById(999L)).thenThrow(new BookNotFoundException("Book not found"));
        
        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }
}
```

## Testing with Database

### Using @Sql for Test Data

```java
@SpringBootTest
@Sql("/test-data.sql")
class BookServiceIntegrationTest {
    
    @Autowired
    private BookService bookService;
    
    @Test
    void shouldFindBooksFromTestData() {
        List<Book> books = bookService.getAllBooks();
        assertThat(books).isNotEmpty();
    }
}
```

### Test Data Script (test-data.sql)
```sql
INSERT INTO authors (name, email) VALUES ('Test Author', 'test@example.com');
INSERT INTO books (title, isbn, price, author_id) VALUES 
    ('Test Book 1', '978-1111111111', 10.99, 1),
    ('Test Book 2', '978-2222222222', 15.99, 1);
```

### Using @DirtiesContext

```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookServiceIntegrationTest {
    // This ensures clean context for each test
}
```

## Testing Configuration

### Test Properties

Create `application-test.properties`:
```properties
# Use different database for tests
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.jpa.hibernate.ddl-auto=create-drop

# Disable logging during tests
logging.level.org.hibernate=WARN
logging.level.org.springframework=WARN
```

### Test Configuration Class

```java
@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneOffset.UTC);
    }
    
    @Bean
    @Primary
    public EmailService mockEmailService() {
        return Mockito.mock(EmailService.class);
    }
}
```

## Advanced Testing Patterns

### Testing Transactions

```java
@SpringBootTest
@Transactional
class TransactionTest {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @Rollback(false) // Don't rollback this test
    void shouldCommitTransaction() {
        Book book = bookService.createBook(new Book("Title", "ISBN"));
        entityManager.flush();
        assertThat(book.getId()).isNotNull();
    }
}
```

### Testing Security

```java
@WebMvcTest(BookController.class)
class BookControllerSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccess() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyUserAccess() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isForbidden());
    }
}
```

### Testing with TestContainers

```java
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class DatabaseIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void shouldConnectToDatabase() {
        assertTrue(postgres.isRunning());
    }
}
```

## Testing Best Practices

### 1. Test Naming
```java
// Good: Descriptive method names
@Test
void shouldReturnEmptyListWhenNoBooksExist() { }

@Test
void shouldThrowExceptionWhenBookNotFound() { }

// Avoid: Generic names
@Test
void testGetBooks() { }
```

### 2. AAA Pattern (Arrange, Act, Assert)
```java
@Test
void shouldCalculateDiscountCorrectly() {
    // Arrange
    Book book = new Book("Title", "ISBN", BigDecimal.valueOf(100));
    BigDecimal discountRate = BigDecimal.valueOf(0.1);
    
    // Act
    BigDecimal discountedPrice = book.applyDiscount(discountRate);
    
    // Assert
    assertThat(discountedPrice).isEqualTo(BigDecimal.valueOf(90));
}
```

### 3. Use Meaningful Assertions
```java
// Good: Specific assertions
assertThat(books).hasSize(3);
assertThat(book.getTitle()).isEqualTo("Expected Title");
assertThat(book.getPrice()).isGreaterThan(BigDecimal.ZERO);

// Avoid: Generic assertions
assertTrue(books.size() == 3);
```

### 4. Test Data Builders
```java
public class BookTestDataBuilder {
    private String title = "Default Title";
    private String isbn = "978-0000000000";
    private BigDecimal price = BigDecimal.valueOf(10.99);
    
    public BookTestDataBuilder withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public BookTestDataBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
    
    public Book build() {
        return new Book(title, isbn, price);
    }
}

// Usage in tests
@Test
void shouldCalculateDiscount() {
    Book book = new BookTestDataBuilder()
            .withTitle("Test Book")
            .withPrice(BigDecimal.valueOf(20.00))
            .build();
    // ... test logic
}
```

## Running Tests

### Maven Commands
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BookServiceTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Skip tests
mvn clean install -DskipTests
```

### IDE Integration
Most IDEs provide excellent test runners:
- IntelliJ IDEA: Right-click and "Run Tests"  
- Eclipse: Run As → JUnit Test
- VS Code: Use Java Test Runner extension

## Test Coverage

### JaCoCo Plugin
Add to pom.xml:
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run coverage:
```bash
mvn test jacoco:report
```

## Summary

Effective testing in Spring applications involves:
- Using appropriate test slices (`@WebMvcTest`, `@DataJpaTest`)
- Mocking external dependencies with `@MockBean`
- Writing meaningful test names and assertions
- Following AAA pattern for test structure
- Using test data builders for complex objects
- Maintaining good test coverage

Testing ensures your Spring applications are robust, maintainable, and behave correctly under various conditions.

## Next Steps

1. Practice with the test examples in the bookstore application
2. Write tests for your own Spring applications
3. Explore advanced testing features like TestContainers
4. Learn about Spring Security testing for secured endpoints