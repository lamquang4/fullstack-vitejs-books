package com.bookstore.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.User;
@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
void deleteByUser(User user);
}