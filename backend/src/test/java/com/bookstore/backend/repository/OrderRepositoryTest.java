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
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private OrderDetailRepository orderDetailRepository;

  private User user;
  private Book book;

  @BeforeEach
  void setup() {

    user =
        userRepository.save(
            User.builder()
                .email("user@gmail.com")
                .fullname("User Test")
                .password("123")
                .role(3)
                .status(1)
                .build());

    Author author =
        authorRepository.save(Author.builder().fullname("Author1").slug("author1").build());

    Publisher publisher =
        publisherRepository.save(Publisher.builder().name("Pub A").slug("pub-a").build());

    Category category =
        categoryRepository.save(
            Category.builder().name("CategoryA").slug("cat-a").status(1).build());

    book =
        bookRepository.save(
            Book.builder()
                .title("Book 1")
                .slug("book-1")
                .price(100.0)
                .discount(0.0)
                .description("desc")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .numberOfPages(100)
                .weight(1.0)
                .width(10.0)
                .length(20.0)
                .thickness(1.0)
                .stock(10)
                .status(1)
                .author(author)
                .publisher(publisher)
                .category(category)
                .build());
  }

  private Order createOrder(String code, int status, LocalDateTime createdAt) {
    return orderRepository.save(
        Order.builder()
            .orderCode(code)
            .fullname("Test")
            .phone("123")
            .speaddress("A")
            .city("C")
            .ward("W")
            .paymethod("COD")
            .total(200.0)
            .status(status)
            .user(user)
            .createdAt(createdAt)
            .build());
  }

  private void addOrderDetail(Order order, int qty) {
    orderDetailRepository.save(
        OrderDetail.builder()
            .order(order)
            .book(book)
            .quantity(qty)
            .price(100.0)
            .discount(0.0)
            .build());
  }

  @Test
  void existsByUser_shouldReturnTrue() {
    createOrder("OD1", 1, LocalDateTime.now());
    assertThat(orderRepository.existsByUser(user)).isTrue();
  }

  @Test
  void existsByUser_shouldReturnFalse() {
    assertThat(orderRepository.existsByUser(user)).isFalse();
  }

  @Test
  void findByOrderCodeContainingIgnoreCase_shouldReturnMatchingOrders() {
    createOrder("ABC001", 1, LocalDateTime.now());
    createOrder("XYZ001", 1, LocalDateTime.now());

    var result =
        orderRepository.findByOrderCodeContainingIgnoreCase(
            "abc", org.springframework.data.domain.PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(1);
  }

  @Test
  void findByUserIdAndOrderCode_shouldReturnOrder() {
    createOrder("OD999", 1, LocalDateTime.now());

    var found = orderRepository.findByUserIdAndOrderCode(user.getId(), "OD999");

    assertThat(found).isPresent();
  }

  @Test
  void findByStatus_shouldReturnCorrectOrders() {
    createOrder("O1", 3, LocalDateTime.now());
    createOrder("O2", 1, LocalDateTime.now());

    var result =
        orderRepository.findByStatus(3, org.springframework.data.domain.PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(1);
  }

  @Test
  void findByCreatedAtBetween_shouldReturnCorrectOrders() {
    LocalDateTime now = LocalDateTime.now();

    createOrder("A", 1, now.minusDays(5));
    createOrder("B", 1, now.minusDays(2));
    createOrder("C", 1, now.plusDays(1));

    var result =
        orderRepository.findByCreatedAtBetween(
            now.minusDays(3),
            now.plusDays(2),
            org.springframework.data.domain.PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(2);
  }

  @Test
  void sumTotalByStatus_shouldReturnCorrectSum() {
    createOrder("A", 3, LocalDateTime.now());
    createOrder("B", 3, LocalDateTime.now());
    createOrder("C", 1, LocalDateTime.now());

    Double total = orderRepository.sumTotalByStatus(3);

    assertThat(total).isEqualTo(400.0);
  }

  @Test
  void sumTotalByStatusAndCreatedAtBetween_shouldReturnCorrectSum() {
    LocalDateTime now = LocalDateTime.now();

    createOrder("A", 3, now.minusDays(3));
    createOrder("B", 3, now.plusDays(1));
    createOrder("C", 3, now.minusDays(10));

    Double total =
        orderRepository.sumTotalByStatusAndCreatedAtBetween(3, now.minusDays(5), now.plusDays(3));

    assertThat(total).isEqualTo(400.0);
  }

  @Test
  void sumQuantityByStatus_shouldReturnCorrectQuantity() {
    Order o1 = createOrder("A", 3, LocalDateTime.now());
    Order o2 = createOrder("B", 1, LocalDateTime.now());

    addOrderDetail(o1, 5);
    addOrderDetail(o2, 9);

    Long qty = orderRepository.sumQuantityByStatus(3);

    assertThat(qty).isEqualTo(5);
  }

  @Test
  void sumQuantityByStatusAndCreatedAtBetween_shouldReturnCorrectQuantity() {
    LocalDateTime now = LocalDateTime.now();

    Order o1 = createOrder("A", 3, now.minusDays(2));
    Order o2 = createOrder("B", 3, now.plusDays(1));
    Order o3 = createOrder("C", 3, now.minusDays(10));

    addOrderDetail(o1, 4);
    addOrderDetail(o2, 6);
    addOrderDetail(o3, 99);

    Long qty =
        orderRepository.sumQuantityByStatusAndCreatedAtBetween(
            3, now.minusDays(5), now.plusDays(5));

    assertThat(qty).isEqualTo(10);
  }

  @Test
  void findByOrderCode_shouldReturnOrder() {
    createOrder("SPECIAL123", 1, LocalDateTime.now());
    var order = orderRepository.findByOrderCode("SPECIAL123");

    assertThat(order).isPresent();
  }

  @Test
  void findByStatusAndCreatedAtBefore_shouldReturnCorrectOrders() {
    LocalDateTime now = LocalDateTime.now();

    createOrder("A", 0, now.minusDays(2));
    createOrder("B", 0, now.plusDays(5));

    var results = orderRepository.findByStatusAndCreatedAtBefore(0, now);

    assertThat(results.size()).isEqualTo(1);
  }
}
