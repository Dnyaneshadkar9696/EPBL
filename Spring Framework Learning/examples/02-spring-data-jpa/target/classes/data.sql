-- Sample data for the bookstore application
-- This file is automatically executed by Spring Boot when spring.sql.init.mode=always is set

-- Insert Authors
INSERT INTO authors (first_name, last_name, email, biography, birth_year, created_at, updated_at) VALUES
('J.K.', 'Rowling', 'jk.rowling@example.com', 'British author, best known for the Harry Potter series', 1965, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('George', 'Orwell', 'george.orwell@example.com', 'English novelist and social critic, known for 1984 and Animal Farm', 1903, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane', 'Austen', 'jane.austen@example.com', 'English novelist known for her wit and social commentary', 1775, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Stephen', 'King', 'stephen.king@example.com', 'American author of horror, supernatural fiction, and fantasy novels', 1947, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Agatha', 'Christie', 'agatha.christie@example.com', 'English detective novelist, known for Hercule Poirot and Miss Marple', 1890, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Books
INSERT INTO books (title, isbn, description, price, publication_date, page_count, genre, stock_quantity, author_id, created_at, updated_at) VALUES
-- J.K. Rowling Books (Author ID: 1)
('Harry Potter and the Philosopher''s Stone', '978-0747532699', 'The first book in the Harry Potter series', 12.99, '1997-06-26', 223, 'FANTASY', 50, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Harry Potter and the Chamber of Secrets', '978-0747538493', 'The second book in the Harry Potter series', 13.99, '1998-07-02', 251, 'FANTASY', 45, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Harry Potter and the Prisoner of Azkaban', '978-0747546290', 'The third book in the Harry Potter series', 14.99, '1999-07-08', 317, 'FANTASY', 40, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- George Orwell Books (Author ID: 2)
('1984', '978-0451524935', 'A dystopian social science fiction novel', 13.99, '1949-06-08', 328, 'SCIENCE_FICTION', 30, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Animal Farm', '978-0451526342', 'An allegorical novella about farm animals', 10.99, '1945-08-17', 95, 'FICTION', 25, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Jane Austen Books (Author ID: 3)
('Pride and Prejudice', '978-0141439518', 'A romantic novel of manners', 11.99, '1813-01-28', 432, 'ROMANCE', 35, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Emma', '978-0141439587', 'A novel about the perils of misconstrued romance', 12.99, '1815-12-23', 474, 'ROMANCE', 20, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Stephen King Books (Author ID: 4)
('The Shining', '978-0307743657', 'A horror novel about a haunted hotel', 15.99, '1977-01-28', 447, 'FICTION', 28, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('IT', '978-1501142970', 'A horror novel about a shapeshifting entity', 18.99, '1986-09-15', 1138, 'FICTION', 22, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Agatha Christie Books (Author ID: 5)
('Murder on the Orient Express', '978-0062693662', 'A detective novel featuring Hercule Poirot', 13.99, '1934-01-01', 256, 'MYSTERY', 15, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('The Murder of Roger Ackroyd', '978-0062073556', 'A Hercule Poirot mystery novel', 12.99, '1926-06-01', 312, 'MYSTERY', 18, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('And Then There Were None', '978-0062073488', 'A mystery novel about ten strangers on an island', 14.99, '1939-11-06', 264, 'MYSTERY', 12, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);