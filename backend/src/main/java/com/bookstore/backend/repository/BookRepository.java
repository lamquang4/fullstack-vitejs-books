package com.bookstore.backend.repository;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    boolean existsByAuthor(Author author);
    boolean existsByPublisher(Publisher publisher);
    boolean existsByCategory(Category category);
    Optional<Book> findByTitle(String title);
    Optional<Book> findBySlugAndStatus(String slug, int status);
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Book> findByCategoryAndStatus(Category category, Integer status);

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
    // category
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


    @Query("SELECT b FROM Book b WHERE b.status = :status")
    Page<Book> findByStatus(@Param("status") int status, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = :status ORDER BY (b.price - b.discount) ASC")
    Page<Book> findByStatusOrderByEffectivePriceAsc(@Param("status") int status, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = :status ORDER BY (b.price - b.discount) DESC")
    Page<Book> findByStatusOrderByEffectivePriceDesc(@Param("status") int status, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = :status AND b.discount > 0")
    Page<Book> findDiscounted(@Param("status") int status, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = :status AND b.discount > 0 ORDER BY (b.price - b.discount) ASC")
    Page<Book> findDiscountedOrderByEffectivePriceAsc(@Param("status") int status, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = :status AND b.discount > 0 ORDER BY (b.price - b.discount) DESC")
    Page<Book> findDiscountedOrderByEffectivePriceDesc(@Param("status") int status, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = :status AND b.category.slug = :slug ORDER BY (b.price - b.discount) ASC")
    Page<Book> findByCategorySlugAndStatusOrderByEffectivePriceAsc(@Param("slug") String slug, @Param("status") int status, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = :status AND b.category.slug = :slug ORDER BY (b.price - b.discount) DESC")
    Page<Book> findByCategorySlugAndStatusOrderByEffectivePriceDesc(@Param("slug") String slug, @Param("status") int status, Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        LEFT JOIN OrderDetail od ON od.book = b
        LEFT JOIN od.order o
        WHERE b.status = :status 
          AND (o.status = 3 OR o.id IS NULL)
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
        GROUP BY b
        ORDER BY SUM(COALESCE(od.quantity, 0)) DESC
    """)

    Page<Book> findByStatusAndPriceRangeOrderByTotalSold(@Param("status") int status,
                                                         @Param("min") Integer min,
                                                         @Param("max") Integer max,
                                                         Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
    """)

    Page<Book> findByStatusAndPriceRange(
            @Param("status") int status,
            @Param("min") Integer min,
            @Param("max") Integer max,
            Pageable pageable
    );

    @Query("""
        SELECT b FROM Book b
        WHERE b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
        ORDER BY (CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) ASC
    """)
    Page<Book> findByStatusAndPriceRangeOrderByEffectivePriceAsc(
            @Param("status") int status,
            @Param("min") Integer min,
            @Param("max") Integer max,
            Pageable pageable
    );

    @Query("""
        SELECT b FROM Book b
        WHERE b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
        ORDER BY (CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) DESC
    """)
    Page<Book> findByStatusAndPriceRangeOrderByEffectivePriceDesc(
            @Param("status") int status,
            @Param("min") Integer min,
            @Param("max") Integer max,
            Pageable pageable
    );

    @Query("""
        SELECT b FROM Book b
        WHERE b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
          AND (
              LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.author.fullname) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.publisher.name) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.category.name) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Book> searchByTitleAuthorPublisherCategoryAndPrice(
            @Param("q") String q,
            @Param("status") Integer status,
            @Param("min") Integer min,
            @Param("max") Integer max,
            Pageable pageable
    );

    @Query("""
        SELECT b FROM Book b
        WHERE b.discount > 0 AND b.status = :status
          AND ((b.price - b.discount) >= COALESCE(:min, 0))
          AND ((b.price - b.discount) <= COALESCE(:max, 999999999))
    """)
    Page<Book> findDiscountedAndPriceRange(@Param("status") int status,
                                           @Param("min") Integer min,
                                           @Param("max") Integer max,
                                           Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.discount > 0 AND b.status = :status
          AND ((b.price - b.discount) >= COALESCE(:min, 0))
          AND ((b.price - b.discount) <= COALESCE(:max, 999999999))
        ORDER BY (b.price - b.discount) ASC
    """)
    Page<Book> findDiscountedAndPriceRangeOrderByEffectivePriceAsc(@Param("status") int status,
                                                                   @Param("min") Integer min,
                                                                   @Param("max") Integer max,
                                                                   Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.discount > 0 AND b.status = :status
          AND ((b.price - b.discount) >= COALESCE(:min, 0))
          AND ((b.price - b.discount) <= COALESCE(:max, 999999999))
        ORDER BY (b.price - b.discount) DESC
    """)
    Page<Book> findDiscountedAndPriceRangeOrderByEffectivePriceDesc(@Param("status") int status,
                                                                    @Param("min") Integer min,
                                                                    @Param("max") Integer max,
                                                                    Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        LEFT JOIN OrderDetail od ON od.book = b
        LEFT JOIN od.order o
        WHERE b.discount > 0 AND b.status = :status AND (o.status = 3 OR o.id IS NULL)
          AND ((b.price - b.discount) >= COALESCE(:min, 0))
          AND ((b.price - b.discount) <= COALESCE(:max, 999999999))
        GROUP BY b
        ORDER BY SUM(COALESCE(od.quantity, 0)) DESC
    """)
    Page<Book> findDiscountedAndPriceRangeOrderByTotalSold(@Param("status") int status,
                                                           @Param("min") Integer min,
                                                           @Param("max") Integer max,
                                                           Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.discount > 0 AND b.status = :status
          AND ((b.price - b.discount) >= COALESCE(:min, 0))
          AND ((b.price - b.discount) <= COALESCE(:max, 999999999))
          AND (
              LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.author.fullname) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.publisher.name) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.category.name) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Book> searchDiscountedActiveCategoryAndPrice(@Param("q") String q,
                                                      @Param("status") int status,
                                                      @Param("min") Integer min,
                                                      @Param("max") Integer max,
                                                      Pageable pageable);

                                                      @Query("""
        SELECT b FROM Book b
        WHERE b.category.slug = :slug AND b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
    """)
    Page<Book> findByCategorySlugAndStatusAndPriceRange(@Param("slug") String slug,
                                                        @Param("status") int status,
                                                        @Param("min") Integer min,
                                                        @Param("max") Integer max,
                                                        Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.category.slug = :slug AND b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
        ORDER BY (CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) ASC
    """)
    Page<Book> findByCategorySlugAndStatusAndPriceRangeOrderByEffectivePriceAsc(@Param("slug") String slug,
                                                                                @Param("status") int status,
                                                                                @Param("min") Integer min,
                                                                                @Param("max") Integer max,
                                                                                Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.category.slug = :slug AND b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
        ORDER BY (CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) DESC
    """)
    Page<Book> findByCategorySlugAndStatusAndPriceRangeOrderByEffectivePriceDesc(@Param("slug") String slug,
                                                                                 @Param("status") int status,
                                                                                 @Param("min") Integer min,
                                                                                 @Param("max") Integer max,
                                                                                 Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        LEFT JOIN OrderDetail od ON od.book = b
        LEFT JOIN od.order o
        WHERE b.category.slug = :slug AND b.status = :status AND (o.status = 3 OR o.id IS NULL)
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
        GROUP BY b
        ORDER BY SUM(COALESCE(od.quantity, 0)) DESC
    """)
    Page<Book> findByCategorySlugAndStatusAndPriceRangeOrderByTotalSold(@Param("slug") String slug,
                                                                        @Param("status") int status,
                                                                        @Param("min") Integer min,
                                                                        @Param("max") Integer max,
                                                                        Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        WHERE b.category.slug = :slug AND b.status = :status
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) >= COALESCE(:min, 0))
          AND ((CASE WHEN b.discount > 0 THEN (b.price - b.discount) ELSE b.price END) <= COALESCE(:max, 999999999))
          AND (
              LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.author.fullname) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.publisher.name) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(b.category.name) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Book> searchByCategoryAndTitleAuthorPublisherCategoryAndPrice(@Param("slug") String slug,
                                                                       @Param("q") String q,
                                                                       @Param("status") int status,
                                                                       @Param("min") Integer min,
                                                                       @Param("max") Integer max,
                                                                       Pageable pageable);

       // bestseller
    @Query("""
        SELECT b
        FROM Book b
        JOIN OrderDetail od ON od.book = b
        JOIN od.order o
        WHERE b.status IN :statuses
          AND o.status = 3
        GROUP BY b
        ORDER BY SUM(od.quantity) DESC
    """)
    Page<Book> findByStatusInOrderByTotalSold(@Param("statuses") List<Integer> statuses, Pageable pageable);

    @Query("""
        SELECT b
        FROM Book b
        LEFT JOIN OrderDetail od ON od.book = b
        LEFT JOIN od.order o
        WHERE b.status = :status AND (o.status = 3 OR o.id IS NULL)
        GROUP BY b
        HAVING SUM(COALESCE(od.quantity, 0)) > 0
        ORDER BY SUM(COALESCE(od.quantity, 0)) DESC
    """)
    Page<Book> findByStatusOrderByTotalSold(@Param("status") Integer status, Pageable pageable);

    @Query("""
        SELECT b
        FROM Book b
        LEFT JOIN OrderDetail od ON od.book = b
        LEFT JOIN od.order o
        WHERE b.status = :status AND b.discount > 0 AND (o.status = 3 OR o.id IS NULL)
        GROUP BY b
        HAVING SUM(COALESCE(od.quantity, 0)) > 0
        ORDER BY SUM(COALESCE(od.quantity, 0)) DESC
    """)
    Page<Book> findDiscountedOrderByTotalSold(@Param("status") int status, Pageable pageable);

    @Query("""
        SELECT b
        FROM Book b
        LEFT JOIN OrderDetail od ON od.book = b
        LEFT JOIN od.order o
        WHERE b.status = :status AND b.category.slug = :slug AND (o.status = 3 OR o.id IS NULL)
        GROUP BY b
        HAVING SUM(COALESCE(od.quantity, 0)) > 0
        ORDER BY SUM(COALESCE(od.quantity, 0)) DESC
    """)
    Page<Book> findByCategorySlugAndStatusOrderByTotalSold(@Param("slug") String slug, @Param("status") int status, Pageable pageable);

}