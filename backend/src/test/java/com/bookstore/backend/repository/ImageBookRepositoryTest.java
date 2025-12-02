package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class ImageBookRepositoryTest {

  @Autowired private ImageBookRepository imageBookRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private AuthorRepository authorRepository;

  @Autowired private PublisherRepository publisherRepository;

  @Autowired private CategoryRepository categoryRepository;

  private Book book;

  @BeforeEach
  void setup() {

    Category category =
        categoryRepository.save(
            Category.builder().name("Fiction").slug("fiction").status(1).build());

    Author author =
        authorRepository.save(Author.builder().fullname("John Writer").slug("john-writer").build());

    Publisher publisher =
        publisherRepository.save(Publisher.builder().name("NXB Kim Dong").slug("kim-dong").build());

    book =
        bookRepository.save(
            Book.builder()
                .title("Harry Potter")
                .slug("harry-potter")
                .price(100.0)
                .discount(0.0)
                .description("desc")
                .publicationDate("2020")
                .numberOfPages(300)
                .weight(1.2)
                .width(10.0)
                .length(20.0)
                .thickness(2.0)
                .stock(10)
                .status(1)
                .category(category)
                .author(author)
                .publisher(publisher)
                .build());
  }

  @Test
  void existsByBook_shouldReturnTrue_whenImagesExist() {
    imageBookRepository.save(ImageBook.builder().book(book).image("img1.jpg").build());

    boolean exists = imageBookRepository.existsByBook(book);

    assertThat(exists).isTrue();
  }

  @Test
  void existsByBook_shouldReturnFalse_whenNoImages() {
    boolean exists = imageBookRepository.existsByBook(book);

    assertThat(exists).isFalse();
  }

  @Test
  void findByBook_shouldReturnAllImagesOfBook() {
    imageBookRepository.save(ImageBook.builder().book(book).image("a.jpg").build());
    imageBookRepository.save(ImageBook.builder().book(book).image("b.jpg").build());

    List<ImageBook> list = imageBookRepository.findByBook(book);

    assertThat(list).hasSize(2);
    assertThat(list).extracting("image").containsExactlyInAnyOrder("a.jpg", "b.jpg");
  }

  @Test
  void findByBook_shouldReturnEmpty_whenBookHasNoImages() {
    List<ImageBook> list = imageBookRepository.findByBook(book);

    assertThat(list).isEmpty();
  }

  @Test
  void deleteByBook_shouldRemoveAllImagesOfBook() {
    imageBookRepository.save(ImageBook.builder().book(book).image("a.jpg").build());
    imageBookRepository.save(ImageBook.builder().book(book).image("b.jpg").build());

    imageBookRepository.deleteByBook(book);

    List<ImageBook> list = imageBookRepository.findByBook(book);

    assertThat(list).isEmpty();
  }
}
