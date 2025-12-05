package com.bookstore.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
class OrderIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;

  @Autowired private UserRepository userRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private CartRepository cartRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private OrderDetailRepository orderDetailRepository;
  @Autowired private PaymentRepository paymentRepository;

  // mock momo service so we don't call real external API
  @MockBean private com.bookstore.backend.service.MomoService momoService;

  private User user;
  private Category category;
  private Author author;
  private Publisher publisher;
  private Book book;
  private Cart cart;

  @BeforeEach
  void setup() {
    // clean up in right order
    paymentRepository.deleteAll();
    orderDetailRepository.deleteAll();
    orderRepository.deleteAll();
    cartRepository.deleteAll();
    bookRepository.deleteAll();
    categoryRepository.deleteAll();
    authorRepository.deleteAll();
    publisherRepository.deleteAll();
    userRepository.deleteAll();

    // create user
    user =
        userRepository.save(
            User.builder()
                .email("test.user@example.com")
                .fullname("Test User")
                .password("pass")
                .role(3)
                .status(1)
                .build());

    // create supporting entities
    author =
        authorRepository.save(
            Author.builder()
                .fullname("Auth A")
                .slug("auth-a")
                .createdAt(LocalDateTime.now())
                .build());

    publisher =
        publisherRepository.save(
            Publisher.builder().name("Pub A").slug("pub-a").createdAt(LocalDateTime.now()).build());

    category =
        categoryRepository.save(
            Category.builder()
                .name("Cat A")
                .slug("cat-a")
                .status(1)
                .createdAt(LocalDateTime.now())
                .build());

    // create book (publicationDate is LocalDate)
    book =
        bookRepository.save(
            Book.builder()
                .title("Book Test")
                .slug("book-test")
                .description("book desc")
                .publicationDate(LocalDate.of(2023, 1, 1))
                .numberOfPages(100)
                .weight(0.5)
                .width(10.0)
                .length(20.0)
                .thickness(1.0)
                .status(1)
                .price(200.0)
                .discount(20.0)
                .stock(50)
                .author(author)
                .publisher(publisher)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build());

    // create a cart for user (used by createOrder COD test)
    cart = cartRepository.save(Cart.builder().user(user).createdAt(LocalDateTime.now()).build());
  }

  // ----------------- Create order COD: stock deducted and cart deleted ---------------
  @Test
  void createOrder_COD_reducesStock_andDeletesCart() throws Exception {
    // build request DTO payload
    Map<String, Object> detail =
        Map.of(
            "bookId", book.getId(),
            "quantity", 2,
            "price", book.getPrice(),
            "discount", book.getDiscount());

    Map<String, Object> payload =
        Map.of(
            "fullname", "Customer",
            "phone", "0361234567",
            "speaddress", "Addr",
            "city", "City",
            "ward", "Ward",
            "paymethod", "cod",
            "items", List.of(detail));

    double expectedFinalPrice = (book.getPrice() - book.getDiscount()) * 2;

    mockMvc
        .perform(
            post("/api/order/user/{userId}", user.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(0)) // COD -> status 0
        .andExpect(jsonPath("$.items[0].bookId").value(book.getId()))
        .andExpect(jsonPath("$.total").value(expectedFinalPrice));

    // book stock decreased (50 - 2)
    Book updated = bookRepository.findById(book.getId()).orElseThrow();
    assertThat(updated.getStock()).isEqualTo(48);

    // cart should be deleted for COD
    assertThat(cartRepository.findByUserId(user.getId())).isNotPresent();
  }

  // ----------------- Create order with momo: status = -1 and stock not deducted ---------------
  @Test
  void createOrder_Momo_statusMinusOne_noStockChange() throws Exception {
    Map<String, Object> detail =
        Map.of(
            "bookId", book.getId(),
            "quantity", 3,
            "price", book.getPrice(),
            "discount", book.getDiscount());

    Map<String, Object> payload =
        Map.of(
            "fullname", "Customer",
            "phone", "0581234567",
            "speaddress", "Addr",
            "city", "City",
            "ward", "Ward",
            "paymethod", "momo",
            "items", List.of(detail));

    mockMvc
        .perform(
            post("/api/order/user/{userId}", user.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(-1)) // momo -> -1
        .andExpect(jsonPath("$.items[0].bookId").value(book.getId()));

    // stock should remain the same (initial 50)
    Book unchanged = bookRepository.findById(book.getId()).orElseThrow();
    assertThat(unchanged.getStock()).isEqualTo(50);
  }

  // ----------------- Get order by id ---------------
  @Test
  void getOrderById_shouldReturnOrder() throws Exception {
    Order order =
        Order.builder()
            .orderCode("GET1")
            .fullname("C")
            .phone("0123")
            .speaddress("A")
            .city("C")
            .ward("W")
            .paymethod("cod")
            .status(0)
            .total(100.0)
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();

    OrderDetail detail =
        OrderDetail.builder()
            .book(book)
            .order(order)
            .quantity(1)
            .price(book.getPrice())
            .discount(book.getDiscount())
            .build();

    order.setItems(List.of(detail));
    order = orderRepository.save(order);

    mockMvc
        .perform(get("/api/order/{id}", order.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderCode").value("GET1"))
        .andExpect(jsonPath("$.items[0].bookId").value(book.getId()));
  }

  // ----------------- Get all orders (basic) ---------------
  @Test
  void getAllOrders_shouldReturnPaged() throws Exception {
    // create two orders
    Order o1 =
        orderRepository.save(
            Order.builder()
                .orderCode("A1")
                .fullname("A")
                .phone("01")
                .speaddress("a")
                .city("c")
                .ward("w")
                .paymethod("cod")
                .status(0)
                .total(10.0)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

    Order o2 =
        orderRepository.save(
            Order.builder()
                .orderCode("A2")
                .fullname("B")
                .phone("02")
                .speaddress("b")
                .city("c")
                .ward("w")
                .paymethod("cod")
                .status(0)
                .total(20.0)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

    mockMvc
        .perform(get("/api/order").param("page", "1").param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orders").isArray())
        .andExpect(jsonPath("$.total").value(2));
  }
}
