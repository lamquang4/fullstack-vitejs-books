package com.bookstore.backend.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.bookstore.backend.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
Optional<Category> findByName(String name);
Page<Category> findByNameContainingIgnoreCase(String q, Pageable pageable);

    @Query("""
           SELECT DISTINCT c
           FROM Category c
           JOIN Book b ON b.category = c
           WHERE c.status = 1 AND b.status = 1
           """)
    List<Category> findActiveCategoriesWithActiveBooks();
}