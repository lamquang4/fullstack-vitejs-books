package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTest {

  @Autowired private BookRepository bookRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private OrderDetailRepository orderDetailRepository;

  private Author author;
  private Category category;
  private Publisher publisher;
  private Book book;

  @BeforeEach
  void setup() {
    author =
        authorRepository.save(
            Author.builder().fullname("Nguyen Van A").slug("nguyen-van-a").build());

    category =
        categoryRepository.save(
            Category.builder().name("Fiction").slug("fiction").status(1).build());

    publisher =
        publisherRepository.save(Publisher.builder().name("NXB Kim Dong").slug("kim-dong").build());

    book =
        bookRepository.save(
            Book.builder()
                .title("Test Book")
                .slug("test-book")
                .price(100.0)
                .discount(20.0)
                .description("desc")
                .publicationDate("2021")
                .numberOfPages(200)
                .weight(1.0)
                .width(10.0)
                .length(20.0)
                .thickness(2.0)
                .status(1)
                .stock(10)
                .category(category)
                .author(author)
                .publisher(publisher)
                .build());
  }

  @Test
  void existsByAuthor_shouldReturnTrue() {
    boolean exists = bookRepository.existsByAuthor(author);
    assertThat(exists).isTrue();
  }

  @Test
  void existsByCategory_shouldReturnTrue() {
    boolean exists = bookRepository.existsByCategory(category);
    assertThat(exists).isTrue();
  }

  @Test
  void existsByPublisher_shouldReturnTrue() {
    boolean exists = bookRepository.existsByPublisher(publisher);
    assertThat(exists).isTrue();
  }

  @Test
  void findBySlugAndStatus_shouldReturnBook() {
    var result = bookRepository.findBySlugAndStatus("test-book", 1);
    assertThat(result).isPresent();
    assertThat(result.get().getTitle()).isEqualTo("Test Book");
  }

  @Test
  void findByDiscountGreaterThanAndStatus_shouldReturnBook() {
    Page<Book> page =
        bookRepository.findByDiscountGreaterThanAndStatus(0, 1, PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent().get(0).getDiscount()).isEqualTo(20.0);
  }

  @Test
  void searchByTitleAuthorPublisherCategory_shouldReturnMatchingBooks() {
    Page<Book> page =
        bookRepository.searchByTitleAuthorPublisherCategory("kim", 1, PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(1);
  }

  @Test
  void findByStatusAndPriceRangeOrderByTotalSold_shouldReturnSortedBooks() {

    /// Tạo user cho order
    User user =
        userRepository.save(
            User.builder()
                .email("test@example.com")
                .fullname("Test User")
                .password("123")
                .role(3)
                .status(1)
                .build());

    Order order =
        orderRepository.save(
            Order.builder()
                .orderCode("OD001")
                .fullname("A")
                .phone("012345")
                .speaddress("x")
                .city("HN")
                .ward("1")
                .paymethod("COD")
                .status(3)
                .total(1.0)
                .user(user)
                .build());

    orderDetailRepository.save(
        OrderDetail.builder()
            .book(book)
            .order(order)
            .price(100.0)
            .discount(20.0)
            .quantity(5)
            .build());

    Page<Book> page =
        bookRepository.findByStatusAndPriceRangeOrderByTotalSold(1, 0, 500, PageRequest.of(0, 10));

    assertThat(page.getTotalElements()).isEqualTo(1);
    assertThat(page.getContent().get(0).getId()).isEqualTo(book.getId());
  }

  @Test
  void findDiscountedAndPriceRangeOrderByEffectivePriceAsc_shouldSortCorrectly() {

    Book book2 =
        bookRepository.save(
            Book.builder()
                .title("Another Book")
                .slug("another-book")
                .price(200.0)
                .discount(50.0)
                .description("desc")
                .publicationDate("2020")
                .numberOfPages(100)
                .weight(1.0)
                .width(10.0)
                .length(20.0)
                .thickness(2.0)
                .status(1)
                .stock(10)
                .category(category)
                .author(author)
                .publisher(publisher)
                .build());

    Page<Book> page =
        bookRepository.findDiscountedAndPriceRangeOrderByEffectivePriceAsc(
            1, 0, 500, PageRequest.of(0, 10));

    assertThat(page.getContent()).hasSize(2);
    assertThat(page.getContent().get(0).getSlug()).isEqualTo("test-book"); // 100 - 20 = 80
    assertThat(page.getContent().get(1).getSlug()).isEqualTo("another-book"); // 150
  }
}
