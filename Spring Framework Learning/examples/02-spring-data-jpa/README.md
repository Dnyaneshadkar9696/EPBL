# Spring Data JPA Bookstore Example

This example demonstrates comprehensive Spring Data JPA features using a bookstore domain model with authors and books.

## What You'll Learn

- JPA entity mapping with relationships
- Spring Data JPA repositories
- Custom query methods and JPQL
- Pagination and sorting
- Transaction management
- Service layer patterns
- REST API integration with JPA

## Project Structure

```
02-spring-data-jpa/
├── pom.xml                          # Maven configuration with JPA dependencies
├── src/main/java/com/example/bookstore/
│   ├── BookstoreApplication.java              # Main application class
│   ├── model/
│   │   ├── Author.java                        # Author entity with validation
│   │   └── Book.java                          # Book entity with relationships
│   ├── repository/
│   │   ├── AuthorRepository.java              # Author repository with custom queries
│   │   └── BookRepository.java                # Book repository with advanced features
│   ├── service/
│   │   └── BookService.java                   # Business logic and transaction management
│   └── controller/
│       └── BookController.java                # REST endpoints
└── src/main/resources/
    ├── application.properties                 # Database and JPA configuration
    └── data.sql                               # Sample data initialization
```

## Key Features Demonstrated

### 1. JPA Entity Mapping
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- Column constraints and validation
- Lifecycle callbacks (`@PrePersist`, `@PreUpdate`)
- One-to-Many and Many-to-One relationships

### 2. Repository Patterns
- Extending `JpaRepository<T, ID>`
- Query methods derived from method names
- Custom JPQL queries with `@Query`
- Native SQL queries
- Projections and DTOs

### 3. Advanced Query Features
- Pagination with `Pageable`
- Sorting with `Sort`
- Dynamic queries with method parameters
- Aggregate functions (COUNT, AVG, SUM)

### 4. Transaction Management
- `@Transactional` annotation
- Read-only transactions for performance
- Custom isolation levels and propagation

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Basic understanding of JPA/Hibernate concepts

## Running the Application

### Option 1: Using Maven

```bash
# Navigate to the example directory
cd "Spring Framework Learning/examples/02-spring-data-jpa"

# Run the application
mvn spring-boot:run
```

### Option 2: Package and Run

```bash
# Package the application
mvn clean package

# Run the JAR file
java -jar target/spring-data-jpa-bookstore-1.0.0.jar
```

## Database Access

The application uses an in-memory H2 database with sample data pre-loaded.

**H2 Console Access:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:bookstore`
- Username: `sa`
- Password: (leave empty)

## API Endpoints

### Books

```bash
# Get all books
curl http://localhost:8080/api/books

# Get books with pagination
curl "http://localhost:8080/api/books?paginated=true&page=0&size=5&sortBy=title&sortDir=asc"

# Get book by ID
curl http://localhost:8080/api/books/1

# Get book by ISBN
curl http://localhost:8080/api/books/isbn/978-0747532699

# Search books by title
curl "http://localhost:8080/api/books/search?title=Harry"

# Get books by genre
curl http://localhost:8080/api/books/genre/FANTASY

# Get books by price range
curl "http://localhost:8080/api/books/price?min=10&max=15"

# Get books in stock
curl http://localhost:8080/api/books/in-stock

# Get newest books
curl "http://localhost:8080/api/books/newest?page=0&size=3"

# Get book statistics
curl http://localhost:8080/api/books/stats

# Get genre statistics
curl http://localhost:8080/api/books/stats/genre/FANTASY
```

### Create and Update Books

```bash
# Create a new book
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Book",
    "isbn": "978-1234567890",
    "description": "A great new book",
    "price": 19.99,
    "genre": "FICTION",
    "stockQuantity": 10,
    "author": {"id": 1}
  }'

# Update a book
curl -X PUT http://localhost:8080/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "isbn": "978-0747532699",
    "price": 15.99,
    "genre": "FANTASY",
    "stockQuantity": 45,
    "author": {"id": 1}
  }'

# Add stock to a book
curl -X POST "http://localhost:8080/api/books/1/stock?quantity=10"

# Remove stock from a book
curl -X DELETE "http://localhost:8080/api/books/1/stock?quantity=5"
```

## Sample Data

The application comes with pre-loaded sample data:

**Authors:**
- J.K. Rowling
- George Orwell  
- Jane Austen
- Stephen King
- Agatha Christie

**Books:**
- Harry Potter series
- 1984, Animal Farm
- Pride and Prejudice, Emma
- The Shining, IT
- Murder on the Orient Express, and more

## Database Schema

### Authors Table
```sql
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    biography TEXT,
    birth_year INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Books Table
```sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    publication_date DATE,
    page_count INTEGER,
    genre VARCHAR(20),
    stock_quantity INTEGER DEFAULT 0,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);
```

## Key Concepts Illustrated

### 1. Repository Query Methods

```java
// Method name query
List<Book> findByTitleContainingIgnoreCase(String title);

// Custom JPQL query
@Query("SELECT b FROM Book b JOIN FETCH b.author")
List<Book> findAllWithAuthor();

// Native SQL query
@Query(value = "SELECT * FROM books WHERE price < ?1", nativeQuery = true)
List<Book> findAffordableBooks(BigDecimal maxPrice);
```

### 2. Pagination and Sorting

```java
// In controller
Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
Page<Book> books = bookService.getAllBooks(pageable);
```

### 3. Transaction Management

```java
@Service
@Transactional
public class BookService {
    
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
```

### 4. Entity Relationships

```java
// In Book entity
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "author_id")
private Author author;

// In Author entity  
@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
private List<Book> books = new ArrayList<>();
```

## Exercises

1. **Add Category Entity**: Create a many-to-many relationship between books and categories
2. **Implement Search**: Add full-text search capabilities
3. **Add Auditing**: Use Spring Data JPA auditing features
4. **Create Custom Repository**: Implement a custom repository with Criteria API
5. **Add Specifications**: Use Spring Data JPA Specifications for dynamic queries

## Testing

Run the included tests:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BookRepositoryTest
```

## Common Issues and Solutions

**Issue: LazyInitializationException**
- Solution: Use `@Transactional` on service methods or fetch joins in queries

**Issue: N+1 Query Problem**
- Solution: Use `JOIN FETCH` in JPQL queries or `@EntityGraph`

**Issue: Database Connection Pool Exhaustion**
- Solution: Properly configure HikariCP settings in application.properties

## Next Steps

1. Explore the H2 console to understand the generated schema
2. Try different query methods and observe the generated SQL
3. Experiment with pagination parameters
4. Test transaction rollback scenarios
5. Move on to Spring Security examples

## Additional Resources

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA 2.2 Specification](https://download.oracle.com/otndocs/jcp/persistence-2_2-mrel-spec/index.html)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)

Happy Learning! 📚