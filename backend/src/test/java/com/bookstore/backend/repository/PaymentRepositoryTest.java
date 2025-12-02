package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.Payment;
import com.bookstore.backend.entities.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PaymentRepositoryTest {

  @Autowired private PaymentRepository paymentRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private UserRepository userRepository;

  private User user;
  private Order order1;
  private Order order2;

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

    order1 =
        orderRepository.save(
            Order.builder()
                .orderCode("ORD001")
                .fullname("User A")
                .phone("111")
                .speaddress("Addr")
                .city("City")
                .ward("Ward")
                .paymethod("COD")
                .status(1)
                .total(100.0)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

    order2 =
        orderRepository.save(
            Order.builder()
                .orderCode("TEST002")
                .fullname("User B")
                .phone("222")
                .speaddress("Addr")
                .city("City")
                .ward("Ward")
                .paymethod("COD")
                .status(1)
                .total(200.0)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build());

    paymentRepository.save(
        Payment.builder()
            .order(order1)
            .paymethod("MOMO")
            .amount(100.0)
            .transactionId("TXN1")
            .status(1)
            .build());

    paymentRepository.save(
        Payment.builder()
            .order(order1)
            .paymethod("MOMO")
            .amount(150.0)
            .transactionId("TXN2")
            .status(0)
            .build());

    paymentRepository.save(
        Payment.builder()
            .order(order2)
            .paymethod("VNPAY")
            .amount(999.0)
            .transactionId("TXN3")
            .status(1)
            .build());
  }

  @Test
  void findByOrderCodeContainingIgnoreCase_shouldReturnCorrectPayments() {
    var result =
        paymentRepository.findByOrder_OrderCodeContainingIgnoreCase(
            "ord", org.springframework.data.domain.PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(2);
  }

  @Test
  void findByStatus_shouldReturnCorrectPayments() {
    var result =
        paymentRepository.findByStatus(1, org.springframework.data.domain.PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(2);
  }

  @Test
  void findByOrderCodeAndStatus_shouldReturnCorrectPayments() {
    var result =
        paymentRepository.findByOrder_OrderCodeContainingIgnoreCaseAndStatus(
            "ord", 1, org.springframework.data.domain.PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(1);
  }

  @Test
  void findFirstByOrderCode_shouldReturnFirstPayment() {
    var result = paymentRepository.findFirstByOrder_OrderCode("ORD001");

    assertThat(result).isPresent();
    assertThat(result.get().getTransactionId()).isEqualTo("TXN1");
  }
}
