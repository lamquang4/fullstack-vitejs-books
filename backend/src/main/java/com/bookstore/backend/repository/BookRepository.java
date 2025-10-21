package com.bookstore.backend.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

Page<Book> findByCategorySlugAndStatus(String slug, Integer status, Pageable pageable);

Page<Book> findByTitleContainingIgnoreCaseAndCategorySlugAndStatus(String title, String slug, Integer status, Pageable pageable);

// lấy sách có discount > 0
Page<Book> findByDiscountGreaterThanAndStatus(int i, int status, Pageable pageable);
Page<Book> findByDiscountGreaterThanAndStatusAndTitleContainingIgnoreCase(
    int discount, int status, String title, Pageable pageable
);

// status = 1
    @Query("SELECT b FROM Book b " +
           "WHERE (:status IS NULL OR b.status = :status) AND (" +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.author.fullname) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.publisher.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.category.name) LIKE LOWER(CONCAT('%', :q, '%'))" +
           ")")
    Page<Book> searchByTitleAuthorPublisherCategory(@Param("q") String q,
                                                    @Param("status") Integer status,
                                                    Pageable pageable);
    // Category
     @Query("SELECT b FROM Book b " +
           "WHERE b.category.slug = :slug AND b.status = :status AND (" +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.author.fullname) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.publisher.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.category.name) LIKE LOWER(CONCAT('%', :q, '%'))" +
           ")")
    Page<Book> searchByCategoryAndTitleAuthorPublisherCategory(@Param("slug") String slug,
                                                               @Param("q") String q,
                                                               @Param("status") Integer status,
                                                       Pageable pageable);
    // Discount
    @Query("SELECT b FROM Book b " +
           "WHERE b.discount > 0 AND b.status = :status AND (" +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.author.fullname) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.publisher.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.category.name) LIKE LOWER(CONCAT('%', :q, '%'))" +
           ")")
    Page<Book> searchDiscountedActiveCategory(@Param("q") String q,
                                              @Param("status") Integer status,
                                              Pageable pageable);

}