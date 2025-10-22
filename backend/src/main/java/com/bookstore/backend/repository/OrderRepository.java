package com.bookstore.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.User;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
 boolean existsByUser(User user);
     Page<Order> findByOrderCodeContainingIgnoreCase(String orderCode, Pageable pageable);
     Optional<Order> findByUserIdAndOrderCode(String userId, String orderCode);
     Page<Order> findByUserIdAndStatus(String userId, Integer status, Pageable pageable);
     Page<Order> findByUserId(String userId, Pageable pageable);
     Page<Order> findByOrderCodeContainingIgnoreCaseAndStatus(String orderCode, Integer status, Pageable pageable);
     Page<Order> findByStatus(Integer status, Pageable pageable);

   
}