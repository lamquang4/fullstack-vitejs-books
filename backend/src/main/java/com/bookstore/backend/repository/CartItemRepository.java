package com.bookstore.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.CartItem;
import org.springframework.stereotype.Repository;
@Repository
public interface  CartItemRepository extends JpaRepository<CartItem, String> {
    boolean existsByBook(Book book);
}
