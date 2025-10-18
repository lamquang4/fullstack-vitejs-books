package com.bookstore.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.User;
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
 boolean existsByUser(User user);
}