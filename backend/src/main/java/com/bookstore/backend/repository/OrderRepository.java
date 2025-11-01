package com.bookstore.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
 boolean existsByUser(User user);
     Page<Order> findByOrderCodeContainingIgnoreCase(String orderCode, Pageable pageable);
     Optional<Order> findByUserIdAndOrderCode(String userId, String orderCode);
     Page<Order> findByUserIdAndStatus(String userId, Integer status, Pageable pageable);
    Page<Order> findByUserIdAndStatusGreaterThanEqual(String userId, int status, Pageable pageable);
     Page<Order> findByOrderCodeContainingIgnoreCaseAndStatus(String orderCode, Integer status, Pageable pageable);
     Page<Order> findByStatus(Integer status, Pageable pageable);

    Page<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findByStatusAndCreatedAtBetween(Integer status, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findByOrderCodeContainingIgnoreCaseAndCreatedAtBetween(String orderCode, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findByOrderCodeContainingIgnoreCaseAndStatusAndCreatedAtBetween(String orderCode, Integer status, LocalDateTime start, LocalDateTime end, Pageable pageable);


         @Query("SELECT o.status AS status, COUNT(o) AS total " +
           "FROM Order o " +
           "WHERE o.status IN :statuses " +
           "GROUP BY o.status")
    List<Object[]> countOrdersByStatus(@Param("statuses") List<Integer> statuses);

       Optional<Order> findByOrderCode(String orderCode);
   
       // tìm đơn hàng status
       List<Order> findByStatusAndCreatedAtBefore(Integer status, LocalDateTime createdAt);

       @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = :status")
    Double sumTotalByStatus(@Param("status") Integer status);

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :start AND :end")
    Double sumTotalByStatusAndCreatedAtBetween(@Param("status") Integer status,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.order.status = :status")
    Long sumQuantityByStatus(@Param("status") Integer status);

    @Query("SELECT SUM(od.quantity) FROM OrderDetail od WHERE od.order.status = :status AND od.order.createdAt BETWEEN :start AND :end")
    Long sumQuantityByStatusAndCreatedAtBetween(@Param("status") Integer status,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);
}