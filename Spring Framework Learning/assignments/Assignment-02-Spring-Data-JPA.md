# Assignment 2: Spring Data JPA - Library Management System

## Objective

Build a comprehensive Library Management System using Spring Data JPA that demonstrates advanced database operations, relationships, and query capabilities.

## Requirements

Create a **Library Management System** with the following specifications:

### 1. Entities and Relationships

#### Member Entity
- `id` (Long, Primary Key)
- `firstName` (String, required)
- `lastName` (String, required)
- `email` (String, unique, required)
- `phoneNumber` (String)
- `membershipDate` (LocalDate, required)
- `membershipType` (Enum: BASIC, PREMIUM, VIP)
- `isActive` (Boolean, default true)

#### Book Entity
- `id` (Long, Primary Key)
- `title` (String, required)
- `isbn` (String, unique, required)
- `description` (Text)
- `publicationYear` (Integer)
- `availableCopies` (Integer, default 0)
- `totalCopies` (Integer, required)
- `category` (Enum: FICTION, NON_FICTION, SCIENCE, HISTORY, TECHNOLOGY, CHILDREN)

#### Author Entity
- `id` (Long, Primary Key)
- `name` (String, required)
- `biography` (Text)
- `birthDate` (LocalDate)
- `nationality` (String)

#### Loan Entity
- `id` (Long, Primary Key)
- `loanDate` (LocalDateTime, required)
- `dueDate` (LocalDateTime, required)
- `returnDate` (LocalDateTime, nullable)
- `status` (Enum: ACTIVE, RETURNED, OVERDUE)
- `fine` (BigDecimal, default 0.00)

### 2. Relationships

- **Book ↔ Author**: Many-to-Many relationship
- **Member ↔ Loan**: One-to-Many relationship
- **Book ↔ Loan**: One-to-Many relationship

### 3. Repository Requirements

#### MemberRepository
```java
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Method name queries
    Optional<Member> findByEmail(String email);
    List<Member> findByMembershipType(MembershipType type);
    List<Member> findByIsActiveTrue();
    List<Member> findByMembershipDateAfter(LocalDate date);
    
    // Custom queries
    @Query("SELECT m FROM Member m WHERE SIZE(m.loans) > :loanCount")
    List<Member> findMembersWithMoreThanLoans(@Param("loanCount") int loanCount);
    
    // Count active members
    long countByIsActiveTrue();
}
```

#### BookRepository
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    // Find available books
    List<Book> findByAvailableCopiesGreaterThan(Integer copies);
    
    // Find by category
    List<Book> findByCategory(Category category);
    
    // Search by title or description
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> searchBooks(@Param("searchTerm") String searchTerm);
    
    // Find books by author name
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContaining(@Param("authorName") String authorName);
    
    // Get most popular books (most loaned)
    @Query("SELECT b, COUNT(l) as loanCount FROM Book b LEFT JOIN b.loans l GROUP BY b ORDER BY loanCount DESC")
    List<Object[]> findMostPopularBooks();
}
```

#### LoanRepository
```java
public interface LoanRepository extends JpaRepository<Loan, Long> {
    // Find active loans
    List<Loan> findByStatus(LoanStatus status);
    
    // Find overdue loans
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < CURRENT_TIMESTAMP")
    List<Loan> findOverdueLoans();
    
    // Find loans by member
    List<Loan> findByMemberOrderByLoanDateDesc(Member member);
    
    // Calculate total fines
    @Query("SELECT SUM(l.fine) FROM Loan l WHERE l.fine > 0")
    BigDecimal getTotalFines();
}
```

### 4. Service Layer Requirements

#### LibraryService
Implement the following business operations:

```java
@Service
@Transactional
public class LibraryService {
    
    // Book operations
    public Book addBook(Book book);
    public void addCopies(Long bookId, int copies);
    public List<Book> searchBooks(String searchTerm);
    public List<Book> findAvailableBooks();
    
    // Member operations
    public Member registerMember(Member member);
    public Member updateMembershipType(Long memberId, MembershipType type);
    public void deactivateMember(Long memberId);
    
    // Loan operations
    public Loan borrowBook(Long memberId, Long bookId);
    public Loan returnBook(Long loanId);
    public List<Loan> getOverdueLoans();
    public BigDecimal calculateFine(Loan loan);
    
    // Statistics
    public long getTotalActiveMembers();
    public long getTotalAvailableBooks();
    public BigDecimal getTotalOutstandingFines();
    public List<Object[]> getMostPopularBooks();
}
```

### 5. REST Endpoints

#### Book Controller
```
GET    /api/books                     - Get all books (with pagination)
GET    /api/books/search?q={term}     - Search books
GET    /api/books/available           - Get available books
GET    /api/books/category/{category} - Get books by category
GET    /api/books/{id}                - Get book by ID
POST   /api/books                     - Add new book
PUT    /api/books/{id}                - Update book
DELETE /api/books/{id}                - Delete book
POST   /api/books/{id}/copies?count={n} - Add copies to book
```

#### Member Controller
```
GET    /api/members                   - Get all members
GET    /api/members/{id}              - Get member by ID
GET    /api/members/{id}/loans        - Get member's loan history
POST   /api/members                   - Register new member
PUT    /api/members/{id}              - Update member
DELETE /api/members/{id}              - Deactivate member
```

#### Loan Controller
```
GET    /api/loans                     - Get all loans
GET    /api/loans/overdue             - Get overdue loans
POST   /api/loans/borrow              - Borrow a book
PUT    /api/loans/{id}/return         - Return a book
GET    /api/loans/statistics          - Get loan statistics
```

### 6. Database Configuration

Use H2 database with the following configuration:

```properties
# Database
spring.datasource.url=jdbc:h2:mem:library
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.sql.init.mode=always
```

### 7. Sample Data (data.sql)

Provide sample data with:
- At least 5 authors
- At least 10 books with author relationships
- At least 8 members with different membership types  
- At least 15 loans (some active, some returned, some overdue)

### 8. Validation Requirements

Implement proper validation:
- Email format validation
- ISBN format validation (ISBN-10 or ISBN-13)
- Date validations (membership date not in future, due dates after loan dates)
- Business rule validations (can't borrow if book unavailable, can't borrow more than membership limit)

### 9. Advanced Features (Bonus)

1. **Pagination and Sorting**: Implement for all list endpoints
2. **Specifications**: Use Spring Data JPA Specifications for dynamic search
3. **Projections**: Create summary DTOs for performance
4. **Auditing**: Add created/updated timestamps automatically
5. **Custom Exceptions**: Create domain-specific exceptions with proper error handling

## Submission Guidelines

### Project Structure
```
library-management/
├── pom.xml
├── src/main/java/com/example/library/
│   ├── LibraryApplication.java
│   ├── model/
│   │   ├── Member.java
│   │   ├── Book.java
│   │   ├── Author.java
│   │   └── Loan.java
│   ├── repository/
│   │   ├── MemberRepository.java
│   │   ├── BookRepository.java
│   │   ├── AuthorRepository.java
│   │   └── LoanRepository.java
│   ├── service/
│   │   └── LibraryService.java
│   ├── controller/
│   │   ├── BookController.java
│   │   ├── MemberController.java
│   │   └── LoanController.java
│   └── dto/
│       └── (response DTOs)
├── src/main/resources/
│   ├── application.properties
│   └── data.sql
└── README.md
```

### Documentation Requirements

Create comprehensive documentation including:
- API endpoint documentation with examples
- Database schema diagram
- Business rules explanation
- How to run and test the application
- Sample API calls using curl or Postman

## Evaluation Criteria

| Criteria | Points | Description |
|----------|---------|-------------|
| **Entity Design** | 20 | Proper JPA mapping, relationships, validation |
| **Repository Implementation** | 25 | Custom queries, method naming, efficiency |
| **Service Layer** | 20 | Business logic, transaction management |
| **REST Controllers** | 15 | Proper HTTP methods, error handling, validation |
| **Database Integration** | 10 | Sample data, configuration, schema |
| **Code Quality** | 5 | Clean code, proper naming, structure |
| **Documentation** | 5 | Clear README, API documentation |
| **Bonus Features** | 15 | Advanced JPA features, error handling |

**Total: 100 points (+15 bonus)**

## Sample API Usage

### Register a Member
```bash
curl -X POST http://localhost:8080/api/members \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john.doe@example.com",
    "phoneNumber": "+1-555-0123",
    "membershipType": "PREMIUM"
  }'
```

### Borrow a Book
```bash
curl -X POST http://localhost:8080/api/loans/borrow \
  -H "Content-Type: application/json" \
  -d '{
    "memberId": 1,
    "bookId": 3
  }'
```

### Search Books
```bash
curl "http://localhost:8080/api/books/search?q=Java"
```

## Testing Requirements

Create tests for:
- Repository methods (using `@DataJpaTest`)
- Service layer business logic (using `@SpringBootTest`)
- Controller endpoints (using `@WebMvcTest`)

## Common Challenges

1. **Circular JSON References**: Use `@JsonManagedReference` and `@JsonBackReference`
2. **LazyInitializationException**: Ensure proper transaction boundaries
3. **N+1 Queries**: Use JOIN FETCH or entity graphs
4. **Constraint Violations**: Handle unique constraint violations gracefully

## Submission Deadline

Complete and submit within **2 weeks** of starting this assignment.

## Resources

- Spring Data JPA Documentation
- H2 Database Documentation  
- Bean Validation (JSR-303) specification
- Example in `examples/02-spring-data-jpa/`

Good luck! 🚀📚