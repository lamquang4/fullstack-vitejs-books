package com.bookstore.backend.repository;

import com.bookstore.backend.entities.Publisher;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, String> {
  Optional<Publisher> findByName(String name);

  Page<Publisher> findByNameContainingIgnoreCase(String q, Pageable pageable);
}
