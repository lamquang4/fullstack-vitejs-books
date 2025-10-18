package com.bookstore.backend.repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
Optional<Category> findByName(String name);
Page<Category> findByNameContainingIgnoreCase(String q, Pageable pageable);
}