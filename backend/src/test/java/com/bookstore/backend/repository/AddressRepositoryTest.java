package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Address;
import com.bookstore.backend.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class AddressRepositoryTest {

  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private UserRepository userRepository;

  private User user1;
  private User user2;
  private Address address1;
  private Address address2;

  @BeforeEach
  void setup() {

    user1 = User.builder()
        .fullname("Nguyen Van A")
        .email("user1@example.com")
        .password("123456")
        .role(3)
        .status(1)
        .createdAt(LocalDateTime.now())
        .build();

    user2 = User.builder()
        .fullname("Tran Thi B")
        .email("user2@example.com")
        .password("654321")
        .role(3)
        .status(1)
        .createdAt(LocalDateTime.now())
        .build();

    userRepository.save(user1);
    userRepository.save(user2);

    address1 = Address.builder()
        .fullname("Nguyen Van A")
        .phone("0909000001")
        .speaddress("123 ABC Street")
        .ward("Ward 1")
        .city("City A")
        .user(user1)
        .createdAt(LocalDateTime.now())
        .build();

    address2 = Address.builder()
        .fullname("Nguyen Van A")
        .phone("0909000002")
        .speaddress("456 DEF Street")
        .ward("Ward 2")
        .city("City A")
        .user(user1)
        .createdAt(LocalDateTime.now())
        .build();

    addressRepository.save(address1);
    addressRepository.save(address2);
  }

  @Test
  void findByUserId_shouldReturnAddressesForUser() {
    List<Address> addresses = addressRepository.findByUserId(user1.getId());

    assertThat(addresses).hasSize(2);
    assertThat(addresses.get(0).getUser().getId()).isEqualTo(user1.getId());
  }

  @Test
  void findByIdAndUserId_shouldReturnCorrectAddress() {
    Optional<Address> found = addressRepository.findByIdAndUserId(address1.getId(), user1.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getPhone()).isEqualTo("0909000001");
  }

  @Test
  void findByIdAndUserId_shouldReturnEmptyWhenUserMismatch() {
    Optional<Address> found = addressRepository.findByIdAndUserId(address1.getId(), user2.getId());

    assertThat(found).isNotPresent();
  }

  @Test
  void deleteByUserId_shouldDeleteAllAddressesOfUser() {
    assertThat(addressRepository.findByUserId(user1.getId())).hasSize(2);

    addressRepository.deleteByUserId(user1.getId());

    assertThat(addressRepository.findByUserId(user1.getId())).isEmpty();
  }
}
