package com.bookstore.backend.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
 boolean existsByAuthor(Author author);
  boolean existsByPublisher(Publisher publisher);
Optional<Book> findByTitle(String title);
Optional<Book> findBySlug(String slug);
 Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Book> findByStatus(Integer status, Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCaseAndStatus(String title, Integer status, Pageable pageable);

Page<Book> findByCategory_SlugAndStatus(String slug, Integer status, Pageable pageable);

Page<Book> findByTitleContainingIgnoreCaseAndCategory_SlugAndStatus(String title, String slug, Integer status, Pageable pageable);

// lấy sách có discount > 0
Page<Book> findByDiscountGreaterThanAndStatus(int i, int status, Pageable pageable);
Page<Book> findByDiscountGreaterThanAndStatusAndTitleContainingIgnoreCase(
    int discount, int status, String title, Pageable pageable
);

}