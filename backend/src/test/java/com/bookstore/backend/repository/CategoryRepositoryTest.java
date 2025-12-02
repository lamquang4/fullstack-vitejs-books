package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.Publisher;
import java.util.List;
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
class CategoryRepositoryTest {

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private AuthorRepository authorRepository;

  @Autowired private PublisherRepository publisherRepository;

  @Autowired private BookRepository bookRepository;

  private Category cat1, cat2;

  @BeforeEach
  void setUp() {
    cat1 =
        categoryRepository.save(
            Category.builder().name("Fiction").slug("fiction").status(1).build());

    cat2 =
        categoryRepository.save(
            Category.builder().name("History").slug("history").status(0).build());
  }

  @Test
  void findByName_shouldReturnCategory() {
    Optional<Category> found = categoryRepository.findByName("Fiction");

    assertThat(found).isPresent();
    assertThat(found.get().getSlug()).isEqualTo("fiction");
  }

  @Test
  void findByName_shouldReturnEmpty_whenNotExists() {
    Optional<Category> found = categoryRepository.findByName("Unknown");

    assertThat(found).isNotPresent();
  }

  @Test
  void findByNameContainingIgnoreCase_shouldReturnPagedResults() {
    Page<Category> page =
        categoryRepository.findByNameContainingIgnoreCase("fi", PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent().get(0).getName()).isEqualTo("Fiction");
  }

  @Test
  void findByStatus_shouldReturnCorrectCategories() {
    Page<Category> page = categoryRepository.findByStatus(1, PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent().get(0).getSlug()).isEqualTo("fiction");
  }

  @Test
  void findByNameContainingIgnoreCaseAndStatus_shouldReturnFilteredResults() {
    Page<Category> page =
        categoryRepository.findByNameContainingIgnoreCaseAndStatus("his", 0, PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent().get(0).getName()).isEqualTo("History");
  }

  @Test
  void findActiveCategoriesWithActiveBooks_shouldReturnOnlyActiveOnes() {

    // Author
    Author author =
        authorRepository.save(Author.builder().fullname("John Writer").slug("john-writer").build());

    // Publisher
    Publisher publisher =
        publisherRepository.save(Publisher.builder().name("Pub House").slug("pub-house").build());

    // Book active (status=1) thuộc category cat1 (status=1)
    bookRepository.save(
        Book.builder()
            .title("Book 1")
            .slug("book-1")
            .price(100.0)
            .discount(0.0)
            .description("desc")
            .publicationDate("2020")
            .numberOfPages(100)
            .weight(1.0)
            .width(1.0)
            .length(1.0)
            .thickness(1.0)
            .stock(10)
            .status(1)
            .category(cat1)
            .author(author)
            .publisher(publisher)
            .build());

    // Category cat2 không có book active nên KHÔNG được trả về

    List<Category> list = categoryRepository.findActiveCategoriesWithActiveBooks();

    assertThat(list).hasSize(1);
    assertThat(list.get(0).getName()).isEqualTo("Fiction");
  }
}
