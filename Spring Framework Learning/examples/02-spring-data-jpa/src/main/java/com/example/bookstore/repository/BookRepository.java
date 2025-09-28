package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import com.example.bookstore.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entities
 * Demonstrates advanced Spring Data JPA query methods and relationships
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // ========== Basic Query Methods ==========
    
    /**
     * Find book by ISBN (unique field)
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Find books by title containing text (case-insensitive)
     */
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find books by genre
     */
    List<Book> findByGenre(Book.Genre genre);
    
    /**
     * Find books by price range
     */
    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find books published after a specific date
     */
    List<Book> findByPublicationDateAfter(LocalDate date);
    
    /**
     * Find books in stock (quantity > 0)
     */
    List<Book> findByStockQuantityGreaterThan(Integer quantity);
    
    // ========== Relationship-based Queries ==========
    
    /**
     * Find books by author
     */
    List<Book> findByAuthor(Author author);
    
    /**
     * Find books by author ID
     */
    List<Book> findByAuthorId(Long authorId);
    
    /**
     * Find books by author's first name
     */
    List<Book> findByAuthorFirstNameContainingIgnoreCase(String firstName);
    
    /**
     * Find books by author's full name
     */
    @Query("SELECT b FROM Book b WHERE CONCAT(b.author.firstName, ' ', b.author.lastName) LIKE %:fullName%")
    List<Book> findByAuthorFullNameContaining(@Param("fullName") String fullName);
    
    // ========== Complex Queries with Multiple Conditions ==========
    
    /**
     * Find books by genre and price range
     */
    List<Book> findByGenreAndPriceBetween(Book.Genre genre, BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find books by author and genre
     */
    List<Book> findByAuthorAndGenre(Author author, Book.Genre genre);
    
    /**
     * Find available books by genre (stock > 0)
     */
    List<Book> findByGenreAndStockQuantityGreaterThan(Book.Genre genre, Integer stockQuantity);
    
    // ========== Custom JPQL Queries ==========
    
    /**
     * Find books with JOIN FETCH to avoid N+1 problem
     */
    @Query("SELECT b FROM Book b JOIN FETCH b.author")
    List<Book> findAllWithAuthor();
    
    /**
     * Search books by title or description
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    /**
     * Find bestselling books (assuming stock quantity indicates sales)
     */
    @Query("SELECT b FROM Book b ORDER BY b.stockQuantity ASC")
    Page<Book> findBestsellers(Pageable pageable);
    
    /**
     * Find newest books
     */
    @Query("SELECT b FROM Book b ORDER BY b.publicationDate DESC")
    Page<Book> findNewestBooks(Pageable pageable);
    
    /**
     * Find books by price range with pagination
     */
    Page<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Find books by multiple genres
     */
    @Query("SELECT b FROM Book b WHERE b.genre IN :genres")
    List<Book> findByGenres(@Param("genres") List<Book.Genre> genres);
    
    // ========== Aggregate Queries ==========
    
    /**
     * Count books by genre
     */
    long countByGenre(Book.Genre genre);
    
    /**
     * Count books by author
     */
    long countByAuthor(Author author);
    
    /**
     * Get average price by genre
     */
    @Query("SELECT AVG(b.price) FROM Book b WHERE b.genre = :genre")
    BigDecimal getAveragePriceByGenre(@Param("genre") Book.Genre genre);
    
    /**
     * Get total stock quantity
     */
    @Query("SELECT SUM(b.stockQuantity) FROM Book b")
    Long getTotalStockQuantity();
    
    /**
     * Get books count by author
     */
    @Query("SELECT b.author, COUNT(b) FROM Book b GROUP BY b.author")
    List<Object[]> getBookCountByAuthor();
    
    // ========== Native SQL Queries ==========
    
    /**
     * Find books using native SQL with pagination
     */
    @Query(value = "SELECT * FROM books WHERE price < :maxPrice ORDER BY price ASC", 
           countQuery = "SELECT count(*) FROM books WHERE price < :maxPrice",
           nativeQuery = true)
    Page<Book> findAffordableBooks(@Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
    
    /**
     * Get book statistics by genre
     */
    @Query(value = """
        SELECT 
            genre,
            COUNT(*) as book_count,
            AVG(price) as avg_price,
            MIN(price) as min_price,
            MAX(price) as max_price,
            SUM(stock_quantity) as total_stock
        FROM books 
        WHERE genre = :genre
        GROUP BY genre
        """, nativeQuery = true)
    Object[] getBookStatisticsByGenre(@Param("genre") String genre);
    
    // ========== Projections ==========
    
    /**
     * Book summary projection interface
     */
    interface BookSummary {
        Long getId();
        String getTitle();
        String getIsbn();
        BigDecimal getPrice();
        String getGenre();
        Integer getStockQuantity();
        
        // Nested projection for author
        AuthorInfo getAuthor();
        
        interface AuthorInfo {
            String getFirstName();
            String getLastName();
            
            default String getFullName() {
                return getFirstName() + " " + getLastName();
            }
        }
    }
    
    /**
     * Find all books as summaries
     */
    List<BookSummary> findAllProjectedBy();
    
    /**
     * Find book summaries by genre
     */
    List<BookSummary> findByGenreProjectedBy(Book.Genre genre);
    
    // ========== Modifying Queries ==========
    
    /**
     * Update stock quantity
     */
    @Query("UPDATE Book b SET b.stockQuantity = :quantity WHERE b.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    int updateStockQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * Update price for books of specific genre
     */
    @Query("UPDATE Book b SET b.price = b.price * :multiplier WHERE b.genre = :genre")
    @org.springframework.data.jpa.repository.Modifying
    int updatePriceByGenre(@Param("genre") Book.Genre genre, @Param("multiplier") BigDecimal multiplier);
    
    /**
     * Delete books out of stock
     */
    @Query("DELETE FROM Book b WHERE b.stockQuantity = 0")
    @org.springframework.data.jpa.repository.Modifying
    int deleteOutOfStockBooks();
}