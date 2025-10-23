package com.bookstore.backend.controller;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

 @GetMapping
 public ResponseEntity<?> getAllCategories(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "12") int limit,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer status
) {
    Page<Category> categoryPage = categoryService.getAllCategories(page, limit, q, status);

    return ResponseEntity.ok(Map.of(
        "categories", categoryPage.getContent(),
        "totalPages", categoryPage.getTotalPages(),
        "total", categoryPage.getTotalElements()
    ));
}

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

@GetMapping("/active")
public ResponseEntity<Map<String, Object>> getActiveCategoriesWithActiveBooks() {
    List<Category> categories = categoryService.getActiveCategoriesWithActiveBooks();
    return ResponseEntity.ok(Map.of("categories", categories));
}

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

        @PatchMapping("/status/{id}")
    public ResponseEntity<?> updateCategoryStatus(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer status = body.get("status");
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        Category updated = categoryService.updateCategoryStatus(id, status);

        return ResponseEntity.ok(Map.of(
                "id", updated.getId(),
                "status", updated.getStatus()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
