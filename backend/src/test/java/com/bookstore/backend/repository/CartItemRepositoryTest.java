package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.CartItem;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.entities.User;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class CartItemRepositoryTest {

  @Autowired private CartItemRepository cartItemRepository;

  @Autowired private CartRepository cartRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private AuthorRepository authorRepository;

  @Autowired private PublisherRepository publisherRepository;

  private User user;
  private Cart cart;
  private Book book;

  @BeforeEach
  void setup() {
    // Tạo User
    user =
        userRepository.save(
            User.builder()
                .email("test@gmail.com")
                .fullname("Test User")
                .password("123456")
                .role(3)
                .status(1)
                .build());

    // Tạo Cart
    cart = cartRepository.save(Cart.builder().user(user).build());

    // Tạo Category
    Category category =
        categoryRepository.save(
            Category.builder().name("Fiction").slug("fiction").status(1).build());

    // Tạo Author
    Author author =
        authorRepository.save(Author.builder().fullname("Author A").slug("author-a").build());

    // Tạo Publisher
    Publisher publisher =
        publisherRepository.save(
            Publisher.builder().name("Publisher A").slug("publisher-a").build());

    // Tạo Book
    book =
        bookRepository.save(
            Book.builder()
                .title("Book A")
                .slug("book-a")
                .price(100.0)
                .discount(0.0)
                .description("Description")
                .publicationDate(LocalDate.of(2024, 5, 1))
                .numberOfPages(120)
                .weight(200.0)
                .width(10.0)
                .length(20.0)
                .thickness(2.0)
                .status(1)
                .stock(50)
                .category(category)
                .author(author)
                .publisher(publisher)
                .build());

    // Tạo CartItem
    cartItemRepository.save(CartItem.builder().cart(cart).book(book).quantity(2).build());
  }

  @Test
  void existsByBook_shouldReturnTrue() {
    boolean exists = cartItemRepository.existsByBook(book);
    assertThat(exists).isTrue();
  }

  @Test
  void existsByBook_shouldReturnFalse_ifBookNotInCart() {
    Book otherBook =
        bookRepository.save(
            Book.builder()
                .title("Book B")
                .slug("book-b")
                .price(150.0)
                .discount(10.0)
                .description("Desc")
                .publicationDate(LocalDate.of(2025, 2, 1))
                .numberOfPages(150)
                .weight(250.0)
                .width(12.0)
                .length(22.0)
                .thickness(3.0)
                .status(1)
                .stock(30)
                .category(categoryRepository.findAll().get(0))
                .author(authorRepository.findAll().get(0))
                .publisher(publisherRepository.findAll().get(0))
                .build());

    boolean exists = cartItemRepository.existsByBook(otherBook);
    assertThat(exists).isFalse();
  }

  @Test
  void findByCartIdAndBookId_shouldReturnCartItem() {
    Optional<CartItem> found = cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getQuantity()).isEqualTo(2);
  }

  @Test
  void findByCartIdAndBookId_shouldReturnEmpty_whenWrongIds() {
    Optional<CartItem> notFound = cartItemRepository.findByCartIdAndBookId("wrong", "wrong");

    assertThat(notFound).isNotPresent();
  }
}
