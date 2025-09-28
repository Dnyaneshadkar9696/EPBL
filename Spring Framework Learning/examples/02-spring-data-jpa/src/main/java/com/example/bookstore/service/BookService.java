package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Book operations
 * Demonstrates transaction management and business logic
 */
@Service
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    // ========== CRUD Operations ==========
    
    /**
     * Get all books with pagination
     */
    @Transactional(readOnly = true)
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    /**
     * Get all books without pagination
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAllWithAuthor(); // Using JOIN FETCH to avoid N+1
    }
    
    /**
     * Get book by ID
     */
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    /**
     * Get book by ISBN
     */
    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    /**
     * Create a new book
     */
    public Book createBook(Book book) {
        // Business logic: Ensure stock quantity is not negative
        if (book.getStockQuantity() != null && book.getStockQuantity() < 0) {
            book.setStockQuantity(0);
        }
        return bookRepository.save(book);
    }
    
    /**
     * Update an existing book
     */
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        // Update fields
        book.setTitle(bookDetails.getTitle());
        book.setIsbn(bookDetails.getIsbn());
        book.setDescription(bookDetails.getDescription());
        book.setPrice(bookDetails.getPrice());
        book.setPublicationDate(bookDetails.getPublicationDate());
        book.setPageCount(bookDetails.getPageCount());
        book.setGenre(bookDetails.getGenre());
        book.setStockQuantity(bookDetails.getStockQuantity());
        book.setAuthor(bookDetails.getAuthor());
        
        return bookRepository.save(book);
    }
    
    /**
     * Delete a book
     */
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
    
    // ========== Search and Filter Operations ==========
    
    /**
     * Search books by title
     */
    @Transactional(readOnly = true)
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Search books by title or description
     */
    @Transactional(readOnly = true)
    public List<Book> searchBooks(String searchTerm) {
        return bookRepository.searchByTitleOrDescription(searchTerm);
    }
    
    /**
     * Get books by genre
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByGenre(Book.Genre genre) {
        return bookRepository.findByGenre(genre);
    }
    
    /**
     * Get books by author ID
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }
    
    /**
     * Get books by price range
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Get books in stock
     */
    @Transactional(readOnly = true)
    public List<Book> getBooksInStock() {
        return bookRepository.findByStockQuantityGreaterThan(0);
    }
    
    /**
     * Get newest books with pagination
     */
    @Transactional(readOnly = true)
    public Page<Book> getNewestBooks(Pageable pageable) {
        return bookRepository.findNewestBooks(pageable);
    }
    
    /**
     * Get bestselling books with pagination
     */
    @Transactional(readOnly = true)
    public Page<Book> getBestsellers(Pageable pageable) {
        return bookRepository.findBestsellers(pageable);
    }
    
    // ========== Business Operations ==========
    
    /**
     * Add stock to a book
     */
    public Book addStock(Long bookId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        book.addStock(quantity);
        return bookRepository.save(book);
    }
    
    /**
     * Remove stock from a book (e.g., when sold)
     */
    public Book removeStock(Long bookId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        if (!book.removeStock(quantity)) {
            throw new RuntimeException("Insufficient stock. Available: " + book.getStockQuantity() + 
                                     ", Requested: " + quantity);
        }
        
        return bookRepository.save(book);
    }
    
    /**
     * Check if book is available for purchase
     */
    @Transactional(readOnly = true)
    public boolean isBookAvailable(Long bookId, int quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        return book.getStockQuantity() != null && book.getStockQuantity() >= quantity;
    }
    
    // ========== Statistics and Analytics ==========
    
    /**
     * Get total number of books
     */
    @Transactional(readOnly = true)
    public long getTotalBookCount() {
        return bookRepository.count();
    }
    
    /**
     * Get number of books by genre
     */
    @Transactional(readOnly = true)
    public long getBookCountByGenre(Book.Genre genre) {
        return bookRepository.countByGenre(genre);
    }
    
    /**
     * Get average price by genre
     */
    @Transactional(readOnly = true)
    public BigDecimal getAveragePriceByGenre(Book.Genre genre) {
        return bookRepository.getAveragePriceByGenre(genre);
    }
    
    /**
     * Get total stock quantity across all books
     */
    @Transactional(readOnly = true)
    public Long getTotalStockQuantity() {
        return bookRepository.getTotalStockQuantity();
    }
    
    // ========== Bulk Operations ==========
    
    /**
     * Update stock quantity for a book
     */
    public void updateStockQuantity(Long bookId, Integer newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        int updatedRows = bookRepository.updateStockQuantity(bookId, newQuantity);
        if (updatedRows == 0) {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
    }
    
    /**
     * Apply discount to books of a specific genre
     */
    public void applyDiscountByGenre(Book.Genre genre, double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        
        BigDecimal multiplier = BigDecimal.valueOf((100 - discountPercentage) / 100);
        bookRepository.updatePriceByGenre(genre, multiplier);
    }
    
    /**
     * Remove out of stock books
     */
    public int removeOutOfStockBooks() {
        return bookRepository.deleteOutOfStockBooks();
    }
}