package com.example.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application for Bookstore Demo
 * 
 * This application demonstrates:
 * - Spring Data JPA with entities and repositories
 * - One-to-Many relationships (Author -> Books)
 * - Custom query methods
 * - Pagination and sorting
 * - Transaction management
 */
@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
        
        System.out.println("\n📚 Bookstore Application Started!");
        System.out.println("🌐 Application URL: http://localhost:8080");
        System.out.println("🗄️  H2 Console: http://localhost:8080/h2-console");
        System.out.println("   JDBC URL: jdbc:h2:mem:bookstore");
        System.out.println("   Username: sa");
        System.out.println("   Password: (leave empty)");
        System.out.println("\n📖 API Endpoints:");
        System.out.println("   GET  /api/books          - List all books");
        System.out.println("   GET  /api/books/{id}     - Get book by ID");
        System.out.println("   POST /api/books          - Create new book");
        System.out.println("   GET  /api/authors        - List all authors");
        System.out.println("   POST /api/authors        - Create new author");
    }
}