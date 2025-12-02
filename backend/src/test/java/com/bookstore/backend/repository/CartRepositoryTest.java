package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {

  @Autowired
  private CartRepository cartRepository;

  @Autowired
  private UserRepository userRepository;

  private User user;
  private Cart cart;

  @BeforeEach
  void setup() {
    // Tạo User để tránh lỗi NOT NULL constraint
    user = userRepository.save(
        User.builder()
            .email("test@gmail.com")
            .fullname("Test User")
            .password("123456")
            .role(3)
            .status(1)
            .build());

    cart = cartRepository.save(Cart.builder().user(user).build());
  }

  @Test
  void findByUserId_shouldReturnCart() {
    Optional<Cart> found = cartRepository.findByUserId(user.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
  }

  @Test
  void findByUserId_shouldReturnEmpty_whenUserNotExists() {
    Optional<Cart> found = cartRepository.findByUserId("invalid-id");

    assertThat(found).isNotPresent();
  }

  @Test
  void deleteByUserId_shouldDeleteCartOfUser() {
    cartRepository.deleteByUserId(user.getId());

    Optional<Cart> found = cartRepository.findById(cart.getId());
    assertThat(found).isNotPresent();
  }

  @Test
  void deleteByUserId_shouldNotAffectOtherCarts() {
    // Create user 2 + cart 2
    User user2 = userRepository.save(
        User.builder()
            .email("user2@gmail.com")
            .fullname("User 2")
            .password("abc123")
            .role(3)
            .status(1)
            .build());

    Cart cart2 = cartRepository.save(Cart.builder().user(user2).build());

    // Xóa cart của user1
    cartRepository.deleteByUserId(user.getId());

    // cart user1 phải bị xóa
    assertThat(cartRepository.findByUserId(user.getId())).isNotPresent();

    // cart user2 phải còn
    assertThat(cartRepository.findByUserId(user2.getId())).isPresent();
  }
}
