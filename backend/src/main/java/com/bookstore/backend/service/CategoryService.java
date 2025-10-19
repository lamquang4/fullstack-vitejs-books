package com.bookstore.backend.service;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.repository.CategoryRepository;
import com.bookstore.backend.utils.SlugUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

public Page<Category> getCategories(int page, int limit, String q) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    if (q != null && !q.isEmpty()) {
        return categoryRepository.findByNameContainingIgnoreCase(q, pageable);
    }
    return categoryRepository.findAll(pageable);
}

    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
    if (categoryRepository.findByName(category.getName()).isPresent()) {
        throw new IllegalArgumentException("Category name already exists");
    }
        category.setSlug(SlugUtil.toSlug(category.getName()));
        return categoryRepository.save(category);
    }

    public Category updateCategory(String id, Category category) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    categoryRepository.findByName(category.getName())
                            .filter(a -> !a.getId().equals(id))
                            .ifPresent(a -> { throw new IllegalArgumentException("Category name already exists"); });
                    category.setSlug(SlugUtil.toSlug(category.getName()));

                    existingCategory.setName(category.getName());
                    existingCategory.setSlug(category.getSlug());
                    return categoryRepository.save(existingCategory);
                })
                .orElse(null);
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
