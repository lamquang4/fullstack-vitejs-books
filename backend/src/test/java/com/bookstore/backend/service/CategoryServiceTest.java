package com.bookstore.backend.service;

import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private BookRepository bookRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setup() {
        category = Category.builder()
                .id("c1")
                .name("Test Category")
                .status(1)
                .build();
    }

    // ===========================================
    // GET ALL CATEGORIES
    // ===========================================

    @Test
    void testGetAllCategories_WithQAndStatus() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findByNameContainingIgnoreCaseAndStatus("abc", 1, pageable))
                .thenReturn(page);

        Page<Category> result = categoryService.getAllCategories(1, 10, "abc", 1);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetAllCategories_WithQOnly() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        when(categoryRepository.findByNameContainingIgnoreCase("abc", pageable))
                .thenReturn(new PageImpl<>(List.of(category)));

        Page<Category> result = categoryService.getAllCategories(1, 10, "abc", null);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetAllCategories_WithStatusOnly() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        when(categoryRepository.findByStatus(1, pageable))
                .thenReturn(new PageImpl<>(List.of(category)));

        Page<Category> result = categoryService.getAllCategories(1, 10, null, 1);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetAllCategories_NoFilter() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        when(categoryRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(category)));

        Page<Category> result = categoryService.getAllCategories(1, 10, null, null);
        assertEquals(1, result.getContent().size());
    }

    // ===========================================
    // CREATE CATEGORY
    // ===========================================

    @Test
    void testCreateCategory_Success() {
        when(categoryRepository.findByName("Test Category"))
                .thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenReturn(category);

        Category result = categoryService.createCategory(category);
        assertEquals("Test Category", result.getName());
    }

    @Test
    void testCreateCategory_DuplicateName() {
        when(categoryRepository.findByName("Test Category"))
                .thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory(category));
    }

    // ===========================================
    // UPDATE CATEGORY
    // ===========================================

    @Test
    void testUpdateCategory_Success() {
        Category newData = Category.builder()
                .name("New Name")
                .status(1)
                .build();

        when(categoryRepository.findById("c1")).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("New Name")).thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenReturn(category);

        Category updated = categoryService.updateCategory("c1", newData);
        assertEquals("New Name", updated.getName());
    }

    @Test
    void testUpdateCategory_NotFound() {
        when(categoryRepository.findById("c1")).thenReturn(Optional.empty());
        assertNull(categoryService.updateCategory("c1", category));
    }

    @Test
    void testUpdateCategory_DuplicateName() {
        Category duplicate = Category.builder()
                .id("c2")
                .name("Dup")
                .build();

        Category updateData = Category.builder()
                .name("Dup")
                .build();

        when(categoryRepository.findById("c1")).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Dup")).thenReturn(Optional.of(duplicate));

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory("c1", updateData));
    }

    @Test
    void testUpdateCategory_HiddenCategory_HidesBooks() {
        category.setStatus(1);

        Category update = Category.builder()
                .name("Hidden Cat")
                .status(0)
                .build();

        Book b1 = Book.builder().id("b1").status(1).build();
        Book b2 = Book.builder().id("b2").status(1).build();

        when(categoryRepository.findById("c1")).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Hidden Cat")).thenReturn(Optional.empty());
        when(bookRepository.findByCategoryAndStatus(category, 1))
                .thenReturn(List.of(b1, b2));
        when(categoryRepository.save(any())).thenReturn(category);

        categoryService.updateCategory("c1", update);

        assertEquals(0, b1.getStatus());
        assertEquals(0, b2.getStatus());
    }

    // ===========================================
    // UPDATE CATEGORY STATUS
    // ===========================================

    @Test
    void testUpdateCategoryStatus_Success() {
        when(categoryRepository.findById("c1")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any())).thenReturn(category);

        Category result = categoryService.updateCategoryStatus("c1", 1);
        assertEquals(1, result.getStatus());
    }

    @Test
    void testUpdateCategoryStatus_HideBooks() {
        Book b = Book.builder().status(1).build();

        when(categoryRepository.findById("c1")).thenReturn(Optional.of(category));
        when(bookRepository.findByCategoryAndStatus(category, 1))
                .thenReturn(List.of(b));
        when(categoryRepository.save(any())).thenReturn(category);

        categoryService.updateCategoryStatus("c1", 0);

        assertEquals(0, b.getStatus());
    }

    @Test
    void testUpdateCategoryStatus_NotFound() {
        when(categoryRepository.findById("c1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateCategoryStatus("c1", 1));
    }

    // ===========================================
    // DELETE CATEGORY
    // ===========================================

    @Test
    void testDeleteCategory_Success() {
        when(categoryRepository.findById("c1")).thenReturn(Optional.of(category));
        when(bookRepository.existsByCategory(category)).thenReturn(false);

        assertDoesNotThrow(() -> categoryService.deleteCategory("c1"));
        verify(categoryRepository).deleteById("c1");
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.findById("c1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.deleteCategory("c1"));
    }

    @Test
    void testDeleteCategory_HasBooks() {
        when(categoryRepository.findById("c1")).thenReturn(Optional.of(category));
        when(bookRepository.existsByCategory(category)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> categoryService.deleteCategory("c1"));
    }
}
