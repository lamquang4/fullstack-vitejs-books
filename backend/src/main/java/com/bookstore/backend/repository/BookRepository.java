package com.bookstore.backend.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Publisher;
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
 boolean existsByAuthor(Author author);
  boolean existsByPublisher(Publisher publisher);
Optional<Book> findByTitle(String title);
}