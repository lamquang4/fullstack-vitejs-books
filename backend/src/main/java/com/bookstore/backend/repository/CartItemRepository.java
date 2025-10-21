package com.bookstore.backend.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.CartItem;
import org.springframework.stereotype.Repository;
@Repository
public interface  CartItemRepository extends JpaRepository<CartItem, String> {
    boolean existsByBook(Book book);
     Optional<CartItem> findByCartIdAndBookId(String cartId, String bookId);
}
