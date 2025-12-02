package com.bookstore.backend.repository;

import com.bookstore.backend.entities.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
  Optional<Cart> findByUserId(String userId);

  void deleteByUserId(String userId);
}
