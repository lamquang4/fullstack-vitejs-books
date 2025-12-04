package com.bookstore.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.OrderDetail;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.PaymentRepository;
import com.bookstore.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderService_GetOrderTest {

  @Mock private OrderRepository orderRepository;
  @Mock private UserRepository userRepository;
  @Mock private BookRepository bookRepository;
  @Mock private CartRepository cartRepository;
  @Mock private PaymentRepository paymentRepository;
  @Mock private MomoService momoService;

  @InjectMocks private OrderService orderService;

  private Order order;
  private User user;
  private Book book;

  @BeforeEach
  void setup() {
    user = User.builder().id("u1").email("test@mail.com").build();

    book = Book.builder().id("b1").title("Book A").stock(10).build();

    OrderDetail detail =
        OrderDetail.builder().book(book).quantity(2).price(100.0).discount(0.0).build();

    order =
        Order.builder()
            .id("o1")
            .orderCode("ABC123")
            .fullname("John")
            .phone("0123456")
            .user(user)
            .items(List.of(detail))
            .total(200.0)
            .status(0)
            .createdAt(LocalDateTime.now())
            .build();
  }

  // Lấy đơn hàng theo id
  @Test
  void testGetOrderById_Success() {
    when(orderRepository.findById("o1")).thenReturn(Optional.of(order));

    OrderDTO dto = orderService.getOrderById("o1");

    assertEquals("o1", dto.getId());
    assertEquals("John", dto.getFullname());
  }

  @Test
  void testGetOrderById_NotFound() {
    when(orderRepository.findById("o1")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById("o1"));
  }

  // Lấy đơn hàng theo id người dùng và orderCode
  @Test
  void testGetOrderByUserAndCode_Success() {
    when(orderRepository.findByUserIdAndOrderCode("u1", "ABC123")).thenReturn(Optional.of(order));

    OrderDTO dto = orderService.getOrderByUserAndCode("u1", "ABC123");

    assertEquals("ABC123", dto.getOrderCode());
  }

  @Test
  void testGetOrderByUserAndCode_NotFound() {
    when(orderRepository.findByUserIdAndOrderCode("u1", "ABC123")).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> orderService.getOrderByUserAndCode("u1", "ABC123"));
  }

  // Lấy đơn hàng theo orderCode
  @Test
  void testGetOrderByOrderCode_Success() {
    when(orderRepository.findByOrderCode("ABC123")).thenReturn(Optional.of(order));

    OrderDTO dto = orderService.getOrderByOrderCode("ABC123");
    assertEquals("ABC123", dto.getOrderCode());
  }

  @Test
  void testGetOrderByOrderCode_NotFound() {
    when(orderRepository.findByOrderCode("ABC123")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> orderService.getOrderByOrderCode("ABC123"));
  }

  // Lấy các đơn hàng của id người dùng
  @Test
  void testGetOrdersByUserAndStatus_Filtered() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<Order> page = new PageImpl<>(List.of(order));

    when(orderRepository.findByUserIdAndStatus("u1", 0, pageable)).thenReturn(page);

    Page<OrderDTO> result = orderService.getOrdersByUserAndStatus("u1", 1, 10, 0);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void testGetOrdersByUserAndStatus_NoStatus() {
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    Page<Order> page = new PageImpl<>(List.of(order));

    when(orderRepository.findByUserIdAndStatusGreaterThanEqual("u1", 0, pageable)).thenReturn(page);

    Page<OrderDTO> result = orderService.getOrdersByUserAndStatus("u1", 1, 10, null);

    assertEquals(1, result.getTotalElements());
  }

  // Lấy doanh thu và tổng số lượng bán ra
  @Test
  void testGetTotalRevenue() {
    when(orderRepository.sumTotalByStatus(3)).thenReturn(5000.0);
    assertEquals(5000.0, orderService.getTotalRevenue());
  }

  @Test
  void testGetTotalRevenue_Null() {
    when(orderRepository.sumTotalByStatus(3)).thenReturn(null);
    assertEquals(0.0, orderService.getTotalRevenue());
  }

  @Test
  void testGetTotalSoldQuantity() {
    when(orderRepository.sumQuantityByStatus(3)).thenReturn(20L);
    assertEquals(20L, orderService.getTotalSoldQuantity());
  }

  @Test
  void testGetTotalSoldQuantity_Null() {
    when(orderRepository.sumQuantityByStatus(3)).thenReturn(null);
    assertEquals(0L, orderService.getTotalSoldQuantity());
  }

  @Test
  void testGetTodayRevenue() {
    when(orderRepository.sumTotalByStatusAndCreatedAtBetween(
            eq(3), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(2000.0);

    assertEquals(2000.0, orderService.getTodayRevenue());
  }

  @Test
  void testGetTodayRevenue_Null() {
    when(orderRepository.sumTotalByStatusAndCreatedAtBetween(eq(3), any(), any())).thenReturn(null);

    assertEquals(0.0, orderService.getTodayRevenue());
  }

  @Test
  void testGetTodaySoldQuantity() {
    when(orderRepository.sumQuantityByStatusAndCreatedAtBetween(eq(3), any(), any()))
        .thenReturn(10L);

    assertEquals(10L, orderService.getTodaySoldQuantity());
  }

  @Test
  void testGetTodaySoldQuantity_Null() {
    when(orderRepository.sumQuantityByStatusAndCreatedAtBetween(eq(3), any(), any()))
        .thenReturn(null);

    assertEquals(0L, orderService.getTodaySoldQuantity());
  }
}
