package com.bookstore.backend.repository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Author;
@Repository
public interface AuthorRepository extends JpaRepository<Author, String> {
  Optional<Author> findByFullname(String fullname);
  Page<Author> findByFullnameContainingIgnoreCase(String q, Pageable pageable);
}