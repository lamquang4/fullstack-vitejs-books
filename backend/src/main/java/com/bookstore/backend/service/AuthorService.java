package com.bookstore.backend.service;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.repository.AuthorRepository;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

  private final AuthorRepository authorRepository;
  private final BookRepository bookRepository;

  public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
    this.authorRepository = authorRepository;
    this.bookRepository = bookRepository;
  }

  // lấy tất cả các authors
  public Page<Author> getAllAuthors(int page, int limit, String q) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    if (q != null && !q.isEmpty()) {
      return authorRepository.findByFullnameContainingIgnoreCase(q, pageable);
    }
    return authorRepository.findAll(pageable);
  }

  // lấy tất cả các authors không phân trang
  public List<Author> getAllAuthors1() {
    return authorRepository.findAll(Sort.by("createdAt").descending());
  }

  // lấy 1 author theo id
  public Author getAuthorById(String id) {
    return authorRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Tác không tìm thấy"));
  }

  // tạo author
  public Author createAuthor(Author author) {
    if (authorRepository.findByFullname(author.getFullname()).isPresent()) {
      throw new IllegalArgumentException("Họ tên của tác giả đã tồn tại");
    }
    author.setSlug(SlugUtil.toSlug(author.getFullname()));
    return authorRepository.save(author);
  }

  // cập nhật author
  public Author updateAuthor(String id, Author author) {
    return authorRepository
        .findById(id)
        .map(
            existingAuthor -> {
              authorRepository
                  .findByFullname(author.getFullname())
                  .filter(a -> !a.getId().equals(id))
                  .ifPresent(
                      a -> {
                        throw new IllegalArgumentException("Họ tên của tác giả đã tồn tại");
                      });

              author.setSlug(SlugUtil.toSlug(author.getFullname()));

              existingAuthor.setFullname(author.getFullname());
              existingAuthor.setSlug(author.getSlug());

              return authorRepository.save(existingAuthor);
            })
        .orElse(null);
  }

  // xóa author
  public void deleteAuthor(String id) {
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tác giả"));

    if (bookRepository.existsByAuthor(author)) {
      throw new IllegalStateException("Tác giả này không thể xóa vì vẫn còn sách liên kết với họ");
    }

    authorRepository.deleteById(id);
  }
}
