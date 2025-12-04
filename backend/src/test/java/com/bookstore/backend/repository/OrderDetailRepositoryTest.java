package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.OrderDetail;
import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.entities.User;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class OrderDetailRepositoryTest {

  @Autowired private OrderDetailRepository orderDetailRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private AuthorRepository authorRepository;

  @Autowired private PublisherRepository publisherRepository;

  @Autowired private OrderRepository orderRepository;

  @Autowired private UserRepository userRepository;

  private Book book;
  private User user;

  @BeforeEach
  void setup() {

    user =
        userRepository.save(
            User.builder()
                .email("test@gmail.com")
                .fullname("Test User")
                .password("123")
                .role(3)
                .status(1)
                .build());

    Author author =
        authorRepository.save(Author.builder().fullname("Author 1").slug("author-1").build());

    Publisher publisher =
        publisherRepository.save(Publisher.builder().name("NXB A").slug("nxb-a").build());

    Category category =
        categoryRepository.save(
            Category.builder().name("Fiction").slug("fiction").status(1).build());

    book =
        bookRepository.save(
            Book.builder()
                .title("Book A")
                .slug("book-a")
                .price(100.0)
                .discount(10.0)
                .description("desc")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .numberOfPages(200)
                .weight(1.0)
                .width(10.0)
                .length(20.0)
                .thickness(2.0)
                .stock(50)
                .status(1)
                .category(category)
                .author(author)
                .publisher(publisher)
                .build());
  }

  @Test
  void existsByBook_shouldReturnTrue_whenOrderDetailExists() {

    Order order =
        orderRepository.save(
            Order.builder()
                .orderCode("OD1")
                .fullname("User")
                .phone("123456")
                .speaddress("Addr")
                .city("City")
                .ward("Ward")
                .paymethod("COD")
                .status(3)
                .total(200.0)
                .user(user)
                .build());

    orderDetailRepository.save(
        OrderDetail.builder()
            .book(book)
            .order(order)
            .quantity(2)
            .price(100.0)
            .discount(10.0)
            .build());

    boolean exists = orderDetailRepository.existsByBook(book);

    assertThat(exists).isTrue();
  }

  @Test
  void existsByBook_shouldReturnFalse_whenNoOrderDetail() {
    boolean exists = orderDetailRepository.existsByBook(book);

    assertThat(exists).isFalse();
  }

  @Test
  void findTotalSoldByBook_shouldReturnCorrectSum() {

    Order order1 =
        orderRepository.save(
            Order.builder()
                .orderCode("OD1")
                .fullname("User1")
                .phone("123")
                .speaddress("A")
                .city("C")
                .ward("W")
                .paymethod("COD")
                .status(3)
                .total(300.0)
                .user(user)
                .build());

    orderDetailRepository.save(
        OrderDetail.builder()
            .order(order1)
            .book(book)
            .quantity(3)
            .price(100.0)
            .discount(10.0)
            .build());

    Order order2 =
        orderRepository.save(
            Order.builder()
                .orderCode("OD2")
                .fullname("User2")
                .phone("666")
                .speaddress("B")
                .city("C2")
                .ward("W2")
                .paymethod("COD")
                .status(1)
                .total(200.0)
                .user(user)
                .build());

    orderDetailRepository.save(
        OrderDetail.builder()
            .order(order2)
            .book(book)
            .quantity(5)
            .price(100.0)
            .discount(0.0)
            .build());

    Long totalSold = orderDetailRepository.findTotalSoldByBook(book.getId());

    assertThat(totalSold).isEqualTo(3);
  }

  @Test
  void findTotalSoldByBook_shouldReturnNull_whenNoCompletedOrders() {

    Long totalSold = orderDetailRepository.findTotalSoldByBook(book.getId());

    assertThat(totalSold).isNull();
  }
}
