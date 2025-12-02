package com.bookstore.backend.service;

import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CategoryRepository;
import com.bookstore.backend.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final BookRepository bookRepository;

  public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
    this.categoryRepository = categoryRepository;
    this.bookRepository = bookRepository;
  }

  // lấy tất cả categories
  public Page<Category> getAllCategories(int page, int limit, String q, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    if (q != null && !q.isEmpty() && status != null) {
      return categoryRepository.findByNameContainingIgnoreCaseAndStatus(q, status, pageable);
    } else if (q != null && !q.isEmpty()) {
      return categoryRepository.findByNameContainingIgnoreCase(q, pageable);
    } else if (status != null) {
      return categoryRepository.findByStatus(status, pageable);
    }
    return categoryRepository.findAll(pageable);
  }

  // lấy tất cả category không phân trang
  public List<Category> getAllCategories1() {
    return categoryRepository.findAll(Sort.by("createdAt").descending());
  }

  // lấy các category có status = 1 và chứa sản phẩm có status = 1
  public List<Category> getActiveCategoriesWithActiveBooks() {
    return categoryRepository.findActiveCategoriesWithActiveBooks();
  }

  // lấy 1 category theo id
  public Optional<Category> getCategoryById(String id) {
    return categoryRepository.findById(id);
  }

  // tạo category
  public Category createCategory(Category category) {
    if (categoryRepository.findByName(category.getName()).isPresent()) {
      throw new IllegalArgumentException("Tên danh mục đã tồn tại");
    }
    category.setSlug(SlugUtil.toSlug(category.getName()));
    return categoryRepository.save(category);
  }

  // cập nhật category
  public Category updateCategory(String id, Category category) {
    return categoryRepository
        .findById(id)
        .map(
            existingCategory -> {
              categoryRepository
                  .findByName(category.getName())
                  .filter(a -> !a.getId().equals(id))
                  .ifPresent(
                      a -> {
                        throw new IllegalArgumentException("Tên danh mục đã tồn tại");
                      });
              category.setSlug(SlugUtil.toSlug(category.getName()));

              existingCategory.setName(category.getName());
              existingCategory.setSlug(category.getSlug());
              existingCategory.setStatus(category.getStatus());

              // Nếu category bị ẩn, ẩn tất cả sách thuộc category
              if (existingCategory.getStatus() == 0) {
                List<Book> books = bookRepository.findByCategoryAndStatus(existingCategory, 1);
                books.forEach(book -> book.setStatus(0));
                bookRepository.saveAll(books);
              }

              return categoryRepository.save(existingCategory);
            })
        .orElse(null);
  }

  // cập nhật status của category
  @Transactional
  public Category updateCategoryStatus(String id, Integer status) {
    return categoryRepository
        .findById(id)
        .map(
            category -> {
              category.setStatus(status);

              // Nếu category bị ẩn, ẩn tất cả sách thuộc category
              if (status == 0) {
                List<Book> books = bookRepository.findByCategoryAndStatus(category, 1);
                books.forEach(book -> book.setStatus(0));
                bookRepository.saveAll(books);
              }

              return categoryRepository.save(category);
            })
        .orElseThrow(() -> new EntityNotFoundException("Danh mục không tìm thấy"));
  }

  // xóa category
  public void deleteCategory(String id) {
    Category category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Danh mục không tìm thấy"));

    if (bookRepository.existsByCategory(category)) {
      throw new IllegalStateException("Danh mục này không thể xóa vì vẫn còn sách liên kết với nó");
    }

    categoryRepository.deleteById(id);
  }
}
