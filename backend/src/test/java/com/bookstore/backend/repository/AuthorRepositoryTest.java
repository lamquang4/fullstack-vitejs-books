package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Author;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class AuthorRepositoryTest {

  @Autowired private AuthorRepository authorRepository;

  private Author author1;
  private Author author2;

  @BeforeEach
  void setup() {
    author1 = Author.builder().fullname("Nguyen Nhat Anh").slug("nguyen-nhat-anh").build();

    author2 = Author.builder().fullname("J. K. Rowling").slug("jk-rowling").build();

    authorRepository.save(author1);
    authorRepository.save(author2);
  }

  @Test
  void findByFullname_shouldReturnAuthor() {
    Optional<Author> result = authorRepository.findByFullname("Nguyen Nhat Anh");

    assertThat(result).isPresent();
    assertThat(result.get().getSlug()).isEqualTo("nguyen-nhat-anh");
  }

  @Test
  void findByFullname_shouldReturnEmpty() {
    Optional<Author> result = authorRepository.findByFullname("Unknown Author");

    assertThat(result).isNotPresent();
  }

  @Test
  void findByFullnameContainingIgnoreCase_shouldReturnMatchingAuthors() {
    Page<Author> result =
        authorRepository.findByFullnameContainingIgnoreCase("nguyen", PageRequest.of(0, 10));

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getFullname()).isEqualTo("Nguyen Nhat Anh");
  }

  @Test
  void findByFullnameContainingIgnoreCase_shouldReturnEmpty() {
    Page<Author> result =
        authorRepository.findByFullnameContainingIgnoreCase("abcdef", PageRequest.of(0, 10));

    assertThat(result.getContent()).isEmpty();
  }
}
