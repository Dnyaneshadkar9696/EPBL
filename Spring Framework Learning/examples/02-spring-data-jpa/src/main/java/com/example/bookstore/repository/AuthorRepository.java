package com.example.bookstore.repository;

import com.example.bookstore.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Author entities
 * Demonstrates various Spring Data JPA query methods
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    // ========== Query Methods Derived from Method Names ==========
    
    /**
     * Find author by email (unique field)
     */
    Optional<Author> findByEmail(String email);
    
    /**
     * Find authors by first name (case-insensitive)
     */
    List<Author> findByFirstNameContainingIgnoreCase(String firstName);
    
    /**
     * Find authors by last name
     */
    List<Author> findByLastNameOrderByFirstNameAsc(String lastName);
    
    /**
     * Find authors by birth year range
     */
    List<Author> findByBirthYearBetween(Integer startYear, Integer endYear);
    
    /**
     * Find authors born after a specific year
     */
    List<Author> findByBirthYearGreaterThan(Integer year);
    
    /**
     * Find authors with biography containing specific text
     */
    List<Author> findByBiographyContainingIgnoreCase(String keyword);
    
    /**
     * Check if author exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Count authors by birth year
     */
    long countByBirthYear(Integer birthYear);
    
    // ========== Custom JPQL Queries ==========
    
    /**
     * Find authors by full name using JPQL
     */
    @Query("SELECT a FROM Author a WHERE a.firstName = :firstName AND a.lastName = :lastName")
    List<Author> findByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName);
    
    /**
     * Find authors with at least one book
     */
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b")
    List<Author> findAuthorsWithBooks();
    
    /**
     * Find authors with more than specified number of books
     */
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) > :bookCount")
    List<Author> findAuthorsWithMoreThanBooks(@Param("bookCount") int bookCount);
    
    /**
     * Find top N most prolific authors (by book count)
     */
    @Query("SELECT a FROM Author a ORDER BY SIZE(a.books) DESC")
    Page<Author> findMostProlificAuthors(Pageable pageable);
    
    /**
     * Search authors by name or email
     */
    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Author> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
    
    // ========== Native SQL Queries ==========
    
    /**
     * Find authors using native SQL query
     * Useful for database-specific operations
     */
    @Query(value = "SELECT * FROM authors WHERE birth_year IS NOT NULL ORDER BY birth_year ASC", 
           nativeQuery = true)
    List<Author> findAuthorsWithBirthYearNative();
    
    /**
     * Get author statistics using native SQL
     */
    @Query(value = """
        SELECT 
            COUNT(*) as total_authors,
            AVG(birth_year) as avg_birth_year,
            MIN(birth_year) as oldest_birth_year,
            MAX(birth_year) as youngest_birth_year
        FROM authors 
        WHERE birth_year IS NOT NULL
        """, nativeQuery = true)
    Object[] getAuthorStatistics();
    
    // ========== Custom Query with Projections ==========
    
    /**
     * Interface-based projection for author summary
     */
    interface AuthorSummary {
        String getFirstName();
        String getLastName();
        String getEmail();
        Integer getBirthYear();
        
        // Spring Data JPA can derive properties from relationships
        default String getFullName() {  
            return getFirstName() + " " + getLastName();
        }
    }
    
    /**
     * Find all authors as summary projections
     */
    List<AuthorSummary> findAllProjectedBy();
    
    /**
     * Find author summaries by birth year
     */
    List<AuthorSummary> findByBirthYearProjectedBy(Integer birthYear);
    
    // ========== Modifying Queries ==========
    
    /**
     * Update author biography
     * Note: Modifying queries should be used in service methods with @Transactional
     */
    @Query("UPDATE Author a SET a.biography = :biography WHERE a.id = :id")
    @org.springframework.data.jpa.repository.Modifying
    int updateBiography(@Param("id") Long id, @Param("biography") String biography);
    
    /**
     * Delete authors without books
     */
    @Query("DELETE FROM Author a WHERE SIZE(a.books) = 0")
    @org.springframework.data.jpa.repository.Modifying
    int deleteAuthorsWithoutBooks();
}