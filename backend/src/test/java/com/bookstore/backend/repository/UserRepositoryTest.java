package com.bookstore.backend.repository;
import static org.assertj.core.api.Assertions.assertThat;
import com.bookstore.backend.entities.User;
import java.util.Arrays;
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
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  private User admin1;
  private User admin2;
  private User customer1;

  @BeforeEach
  void setup() {
    admin1 =
        userRepository.save(
            User.builder()
                .email("admin1@example.com")
                .fullname("Admin One")
                .password("pass")
                .role(1)
                .status(1)
                .build());

    admin2 =
        userRepository.save(
            User.builder()
                .email("admin2@example.com")
                .fullname("Admin Two")
                .password("pass")
                .role(1)
                .status(0)
                .build());

    customer1 =
        userRepository.save(
            User.builder()
                .email("customer@example.com")
                .fullname("Cust One")
                .password("pass")
                .role(3)
                .status(1)
                .build());
  }

  @Test
  void findByEmail_shouldReturnUser() {
    Optional<User> result = userRepository.findByEmail("admin1@example.com");

    assertThat(result).isPresent();
    assertThat(result.get().getFullname()).isEqualTo("Admin One");
  }

  @Test
  void findByEmail_shouldReturnEmpty_whenNotExists() {
    Optional<User> result = userRepository.findByEmail("notfound@example.com");

    assertThat(result).isNotPresent();
  }

  @Test
  void findByRoleIn_shouldReturnMatchingRoles() {
    Page<User> result = userRepository.findByRoleIn(Arrays.asList(1), PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(2); // admin1 + admin2
  }

  @Test
  void findByRoleIn_shouldReturnEmpty_whenNoMatch() {
    Page<User> result = userRepository.findByRoleIn(Arrays.asList(99), PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  void findByEmailContainingIgnoreCaseAndRoleIn_shouldReturnUsers() {
    Page<User> result =
        userRepository.findByEmailContainingIgnoreCaseAndRoleIn(
            "admin", Arrays.asList(1), PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(2);
  }

  @Test
  void findByEmailContainingIgnoreCaseAndRoleIn_shouldReturnEmpty() {
    Page<User> result =
        userRepository.findByEmailContainingIgnoreCaseAndRoleIn(
            "xyz", Arrays.asList(1), PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  void findByRoleInAndStatus_shouldReturnMatchingUsers() {
    Page<User> result =
        userRepository.findByRoleInAndStatus(Arrays.asList(1), 1, PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(1); // only admin1
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("admin1@example.com");
  }

  @Test
  void findByRoleInAndStatus_shouldReturnEmpty_whenNoMatch() {
    Page<User> result =
        userRepository.findByRoleInAndStatus(Arrays.asList(1), 2, PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isZero();
  }

  @Test
  void findByEmailContainingIgnoreCaseAndRoleInAndStatus_shouldReturnMatching() {
    Page<User> result =
        userRepository.findByEmailContainingIgnoreCaseAndRoleInAndStatus(
            "admin", Arrays.asList(1), 1, PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getEmail()).isEqualTo("admin1@example.com");
  }

  @Test
  void findByEmailContainingIgnoreCaseAndRoleInAndStatus_shouldReturnEmpty() {
    Page<User> result =
        userRepository.findByEmailContainingIgnoreCaseAndRoleInAndStatus(
            "admin", Arrays.asList(1), 99, PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isZero();
  }
}
