package com.bookstore.backend.service;
import com.bookstore.backend.entities.Author;
import com.bookstore.backend.repository.AuthorRepository;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.util.Optional;

@Service
public class AuthorService {

  private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
       this.bookRepository = bookRepository;
    }

    // lấy tất cả các authors
public Page<Author> getAuthors(int page, int limit, String q) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    if (q != null && !q.isEmpty()) {
        return authorRepository.findByFullnameContainingIgnoreCase(q, pageable);
    }
    return authorRepository.findAll(pageable);
}

// lấy 1 author theo id
 public Optional<Author> getAuthorById(String id) {
        return authorRepository.findById(id);
    }

    // tạo author
    public Author createAuthor(Author author) {
         if (authorRepository.findByFullname(author.getFullname()).isPresent()) {
        throw new IllegalArgumentException("Author fullname already exists");
    }
         author.setSlug(SlugUtil.toSlug(author.getFullname()));
        return authorRepository.save(author);
    }

    // cập nhật author
    public Author updateAuthor(String id, Author author) {
    return authorRepository.findById(id)
                .map(existingAuthor -> {
                    authorRepository.findByFullname(author.getFullname())
                            .filter(a -> !a.getId().equals(id))
                            .ifPresent(a -> { throw new IllegalArgumentException("Author fullname already exists"); });

                    author.setSlug(SlugUtil.toSlug(author.getFullname()));

                    existingAuthor.setFullname(author.getFullname());
                    existingAuthor.setSlug(author.getSlug());

                    return authorRepository.save(existingAuthor);
                })
                .orElse(null);
    }

    // xóa author
    public void deleteAuthor(String id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found author"));

        if (bookRepository.existsByAuthor(author)) {
            throw new IllegalStateException("This author cannot be deleted as they have books associated with them");
        }
        
        authorRepository.deleteById(id);
    }
}
