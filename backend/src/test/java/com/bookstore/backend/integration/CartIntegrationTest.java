package com.bookstore.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
class CartIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private UserRepository userRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private CartRepository cartRepository;
  @Autowired private OrderDetailRepository orderDetailRepository;
  @Autowired private ImageBookRepository imageBookRepository;
  @Autowired private CartItemRepository cartItemRepository;
  @Autowired private ObjectMapper mapper;

  private User user;
  private Book book;
  private Cart cart;
  private CartItem item;

  @BeforeEach
  void setup() {

    cartItemRepository.deleteAll();
    cartRepository.deleteAll();

    imageBookRepository.deleteAll();
    orderDetailRepository.deleteAll();

    bookRepository.deleteAll();

    categoryRepository.deleteAll();
    authorRepository.deleteAll();
    publisherRepository.deleteAll();

    userRepository.deleteAll();

    // -------------- tạo mới dữ liệu hợp lệ ----------------
    user =
        userRepository.save(
            User.builder()
                .email("test@gmail.com")
                .fullname("Test User")
                .password("123456")
                .role(3)
                .status(1)
                .build());

    Author author =
        authorRepository.save(Author.builder().fullname("Author A").slug("author-a").build());

    Publisher publisher =
        publisherRepository.save(
            Publisher.builder().name("Publisher A").slug("publisher-a").build());

    Category category =
        categoryRepository.save(
            Category.builder().name("Category A").slug("category-a").status(1).build());

    book =
        bookRepository.save(
            Book.builder()
                .title("Book A")
                .slug("book-a")
                .description("desc")
                .publicationDate(LocalDate.of(2025, 1, 1))
                .numberOfPages(100)
                .weight(0.5)
                .width(10.0)
                .length(20.0)
                .thickness(1.0)
                .status(1)
                .price(100.0)
                .discount(10.0)
                .stock(20)
                .author(author)
                .publisher(publisher)
                .category(category)
                .build());

    cart = cartRepository.save(Cart.builder().user(user).createdAt(LocalDateTime.now()).build());

    item = cartItemRepository.save(CartItem.builder().cart(cart).book(book).quantity(3).build());
  }

  // ------------------- ADD ITEM -------------------------
  @Test
  void addItemToCart_shouldIncreaseQuantity() throws Exception {
    mockMvc
        .perform(
            post("/api/cart")
                .param("userId", user.getId())
                .param("bookId", book.getId())
                .param("quantity", "2")
                .with(csrf()))
        .andExpect(status().isOk());

    CartItem updated =
        cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId()).orElseThrow();

    assertThat(updated.getQuantity()).isEqualTo(5);
  }

  @Test
  void addItemToCart_shouldCreateCartItemWhenNotExists() throws Exception {
    // xóa item cũ
    cartItemRepository.deleteAll();

    mockMvc
        .perform(
            post("/api/cart")
                .param("userId", user.getId())
                .param("bookId", book.getId())
                .param("quantity", "4")
                .with(csrf()))
        .andExpect(status().isOk());

    CartItem created =
        cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId()).orElseThrow();

    assertThat(created.getQuantity()).isEqualTo(4);
  }

  @Test
  void addItemToCart_shouldLimitMax15() throws Exception {
    item.setQuantity(14);
    cartItemRepository.save(item);

    book.setStock(999);
    bookRepository.save(book);

    mockMvc
        .perform(
            post("/api/cart")
                .param("userId", user.getId())
                .param("bookId", book.getId())
                .param("quantity", "10") // 14 + 10 = 24 → capped to 15
                .with(csrf()))
        .andExpect(status().isOk());

    CartItem updated =
        cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId()).orElseThrow();

    assertThat(updated.getQuantity()).isEqualTo(15);
  }

  // ------------------- UPDATE ITEM -------------------------
  @Test
  void updateCartItemQuantity_shouldUpdateQuantity() throws Exception {
    mockMvc
        .perform(put("/api/cart/item/{id}", item.getId()).param("quantity", "7").with(csrf()))
        .andExpect(status().isOk());

    CartItem updated = cartItemRepository.findById(item.getId()).orElseThrow();
    assertThat(updated.getQuantity()).isEqualTo(7);
  }

  @Test
  void updateCartItemQuantity_shouldDeleteWhenZero() throws Exception {
    mockMvc
        .perform(put("/api/cart/item/{id}", item.getId()).param("quantity", "0").with(csrf()))
        .andExpect(status().isOk());

    assertThat(cartItemRepository.findById(item.getId())).isEmpty();
  }

  // ------------------- REMOVE ITEM -------------------------
  @Test
  void removeItemFromCart_shouldDeleteItem() throws Exception {
    mockMvc
        .perform(delete("/api/cart/item/{id}", item.getId()).with(csrf()))
        .andExpect(status().isOk());

    assertThat(cartItemRepository.findById(item.getId())).isNotPresent();
  }
}
