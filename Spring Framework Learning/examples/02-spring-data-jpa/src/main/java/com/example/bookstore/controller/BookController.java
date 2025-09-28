package com.example.bookstore.controller;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Book operations
 * Demonstrates Spring Data JPA integration with REST endpoints
 */
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") // For development only
public class BookController {
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    // ========== CRUD Endpoints ==========
    
    /**
     * Get all books with optional pagination
     * Examples:
     * - GET /api/books
     * - GET /api/books?page=0&size=10&sort=title,asc
     */
    @GetMapping
    public ResponseEntity<Object> getAllBooks(
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        if (paginated) {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Book> books = bookService.getAllBooks(pageable);
            return ResponseEntity.ok(books);
        } else {
            List<Book> books = bookService.getAllBooks();
            return ResponseEntity.ok(books);
        }
    }
    
    /**
     * Get book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get book by ISBN
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Optional<Book> book = bookService.getBookByIsbn(isbn);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new book
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update an existing book
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a book
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== Search and Filter Endpoints ==========
    
    /**
     * Search books by title
     * Example: GET /api/books/search?title=Harry
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String searchTerm) {
        
        List<Book> books;
        if (title != null && !title.trim().isEmpty()) {
            books = bookService.searchBooksByTitle(title);
        } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            books = bookService.searchBooks(searchTerm);
        } else {
            books = bookService.getAllBooks();
        }
        
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get books by genre
     * Example: GET /api/books/genre/FANTASY
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable Book.Genre genre) {
        List<Book> books = bookService.getBooksByGenre(genre);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get books by author
     * Example: GET /api/books/author/1
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        List<Book> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get books by price range
     * Example: GET /api/books/price?min=10&max=20
     */
    @GetMapping("/price")
    public ResponseEntity<List<Book>> getBooksByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        
        if (min.compareTo(max) > 0) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Book> books = bookService.getBooksByPriceRange(min, max);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get books in stock
     */
    @GetMapping("/in-stock")
    public ResponseEntity<List<Book>> getBooksInStock() {
        List<Book> books = bookService.getBooksInStock();
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get newest books
     * Example: GET /api/books/newest?page=0&size=5
     */
    @GetMapping("/newest")
    public ResponseEntity<Page<Book>> getNewestBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getNewestBooks(pageable);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get bestsellers
     * Example: GET /api/books/bestsellers?page=0&size=5
     */
    @GetMapping("/bestsellers")
    public ResponseEntity<Page<Book>> getBestsellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getBestsellers(pageable);
        return ResponseEntity.ok(books);
    }
    
    // ========== Business Operation Endpoints ==========
    
    /**
     * Add stock to a book
     * POST /api/books/1/stock?quantity=10
     */
    @PostMapping("/{id}/stock")
    public ResponseEntity<Book> addStock(@PathVariable Long id, @RequestParam int quantity) {
        try {
            Book book = bookService.addStock(id, quantity);
            return ResponseEntity.ok(book);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Remove stock from a book
     * DELETE /api/books/1/stock?quantity=5
     */
    @DeleteMapping("/{id}/stock")
    public ResponseEntity<Book> removeStock(@PathVariable Long id, @RequestParam int quantity) {
        try {
            Book book = bookService.removeStock(id, quantity);
            return ResponseEntity.ok(book);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Check if book is available
     * GET /api/books/1/available?quantity=2
     */
    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> isBookAvailable(@PathVariable Long id, @RequestParam int quantity) {
        try {
            boolean available = bookService.isBookAvailable(id, quantity);
            return ResponseEntity.ok(available);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== Statistics Endpoints ==========
    
    /**
     * Get book statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Object> getBookStatistics() {
        long totalBooks = bookService.getTotalBookCount();
        Long totalStock = bookService.getTotalStockQuantity();
        
        return ResponseEntity.ok(new BookStats(totalBooks, totalStock != null ? totalStock : 0L));
    }
    
    /**
     * Get statistics by genre
     */
    @GetMapping("/stats/genre/{genre}")
    public ResponseEntity<Object> getGenreStatistics(@PathVariable Book.Genre genre) {
        long count = bookService.getBookCountByGenre(genre);
        BigDecimal avgPrice = bookService.getAveragePriceByGenre(genre);
        
        return ResponseEntity.ok(new GenreStats(genre.toString(), count, avgPrice));
    }
    
    // ========== Helper Classes ==========
    
    public static class BookStats {
        private final long totalBooks;
        private final long totalStock;
        
        public BookStats(long totalBooks, long totalStock) {
            this.totalBooks = totalBooks;
            this.totalStock = totalStock;
        }
        
        public long getTotalBooks() { return totalBooks; }
        public long getTotalStock() { return totalStock; }
    }
    
    public static class GenreStats {
        private final String genre;
        private final long count;
        private final BigDecimal averagePrice;
        
        public GenreStats(String genre, long count, BigDecimal averagePrice) {
            this.genre = genre;
            this.count = count;
            this.averagePrice = averagePrice;
        }
        
        public String getGenre() { return genre; }
        public long getCount() { return count; }
        public BigDecimal getAveragePrice() { return averagePrice; }
    }
}