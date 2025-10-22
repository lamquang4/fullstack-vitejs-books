package com.bookstore.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    boolean existsByBook(Book book);

@Query("""
    SELECT SUM(od.quantity)
    FROM OrderDetail od
    WHERE od.book.id = :bookId AND od.order.status = 3
""")
Long findTotalSoldByBook(@Param("bookId") String bookId);

}
