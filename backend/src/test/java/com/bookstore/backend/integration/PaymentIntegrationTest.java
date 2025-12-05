package com.bookstore.backend.integration;

import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
public class PaymentIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @Autowired private UserRepository userRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private PublisherRepository publisherRepository;
    @Autowired private BookRepository bookRepository;

    private User user;
    private Order order;

    @BeforeEach
    void setup() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(
                User.builder()
                        .email("pay@test.com")
                        .fullname("Payment User")
                        .password("123")
                        .role(3)
                        .status(1)
                        .build());

        Author author = authorRepository.save(
                Author.builder().fullname("Auth A").slug("auth-a").createdAt(LocalDateTime.now()).build());

        Publisher publisher = publisherRepository.save(
                Publisher.builder().name("Pub A").slug("pub-a").createdAt(LocalDateTime.now()).build());

        Category category = categoryRepository.save(
                Category.builder().name("Cat A").slug("cat-a").status(1).createdAt(LocalDateTime.now()).build());

        Book book = bookRepository.save(
                Book.builder()
                        .title("Book P")
                        .slug("book-p")
                        .description("pay book")
                        .publicationDate(LocalDate.of(2024, 1, 1))
                        .price(200.0)
                        .discount(20.0)
                        .stock(50)
                        .numberOfPages(100)
                        .weight(0.5)
                        .width(10.0)
                        .length(20.0)
                        .thickness(1.0)
                        .author(author)
                        .publisher(publisher)
                        .category(category)
                        .createdAt(LocalDateTime.now())
                        .status(1)
                        .build());

        order = orderRepository.save(
                Order.builder()
                        .orderCode("PAY1")
                        .fullname("Customer")
                        .phone("0361234567")
                        .speaddress("Addr")
                        .city("City")
                        .ward("Ward")
                        .paymethod("momo")
                        .user(user)
                        .status(1)
                        .total(100.0)
                        .createdAt(LocalDateTime.now())
                        .build());
    }

    // ------------------- 1. getAllPayments no filter ----------------------
    @Test
    void getAllPayments_shouldReturnAll() throws Exception {
        paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .paymethod("momo")
                        .transactionId("tx-111")
                        .amount(100.0)
                        .status(1)
                        .build());

        mockMvc.perform(get("/api/payment")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments").isArray())
                .andExpect(jsonPath("$.total").value(1));
    }

    // ------------------- 2. filter by orderCode ----------------------
    @Test
    void getAllPayments_filterByOrderCode() throws Exception {
        paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .paymethod("momo")
                        .transactionId("tx-222")
                        .amount(100.0)
                        .status(1)
                        .build());

        mockMvc.perform(get("/api/payment")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("q", "PAY1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].orderCode").value("PAY1"))
                .andExpect(jsonPath("$.total").value(1));
    }

    // ------------------- 3. filter by status ----------------------
    @Test
    void getAllPayments_filterByStatus() throws Exception {
        paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .paymethod("momo")
                        .transactionId("tx-333")
                        .amount(100.0)
                        .status(1)
                        .build());

        mockMvc.perform(get("/api/payment")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].status").value(1))
                .andExpect(jsonPath("$.total").value(1));
    }

    // ------------------- 4. filter by both orderCode + status ----------------------
    @Test
    void getAllPayments_filterByOrderCodeAndStatus() throws Exception {
        paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .paymethod("momo")
                        .transactionId("tx-444")
                        .amount(100.0)
                        .status(1)
                        .build());

        mockMvc.perform(get("/api/payment")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("q", "PAY1")
                        .param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].orderCode").value("PAY1"))
                .andExpect(jsonPath("$.payments[0].status").value(1))
                .andExpect(jsonPath("$.total").value(1));
    }

    // ------------------- 5. create payment ----------------------
    @Test
    void createPayment_shouldSaveSuccessfully() {
        Payment payment = paymentRepository.save(
                Payment.builder()
                        .order(order)
                        .paymethod("momo")
                        .transactionId("tx-999")
                        .amount(150.0)
                        .status(1)
                        .build());

        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getOrder().getOrderCode()).isEqualTo("PAY1");
    }
}
