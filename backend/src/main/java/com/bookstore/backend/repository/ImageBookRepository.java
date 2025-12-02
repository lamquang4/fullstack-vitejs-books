package com.bookstore.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.ImageBook;

@Repository
public interface ImageBookRepository extends JpaRepository<ImageBook, String> {
    boolean existsByBook(Book book);

    void deleteByBook(Book book);

    List<ImageBook> findByBook(Book book);
}
