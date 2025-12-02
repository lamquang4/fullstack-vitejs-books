package com.bookstore.backend.repository;

import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.ImageBook;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageBookRepository extends JpaRepository<ImageBook, String> {
  boolean existsByBook(Book book);

  void deleteByBook(Book book);

  List<ImageBook> findByBook(Book book);
}
