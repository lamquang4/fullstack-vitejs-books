package com.bookstore.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class OrderService_UpdateOrderStatusTest {

  @Mock private OrderRepository orderRepository;
  @Mock private UserRepository userRepository;
  @Mock private BookRepository bookRepository;
  @Mock private CartRepository cartRepository;
  @Mock private PaymentRepository paymentRepository;
  @Mock private MomoService momoService;

  @InjectMocks private OrderService orderService;

  private Order order;
  private Book book;
  private OrderDetail detail;
  private Payment payment;

  @BeforeEach
  void setup() {

    book = Book.builder().id("b1").title("Java Book").stock(10).build();

    detail =
        OrderDetail.builder().id("d1").book(book).quantity(3).price(100.0).discount(0.0).build();

    order =
        Order.builder()
            .id("o1")
            .orderCode("ORD123")
            .status(1) // đang xử lý
            .user(User.builder().id("u1").build())
            .items(List.of(detail))
            .total(300.0)
            .build();

    payment =
        Payment.builder()
            .id("p1")
            .order(order)
            .paymethod("momo")
            .transactionId("tx123")
            .status(1)
            .amount(300.0)
            .build();
  }

  // Đơn hàng không tìm thấy
  @Test
  void testUpdateOrderStatus_OrderNotFound() {

    when(orderRepository.findById("o1")).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> orderService.updateOrderStatus("o1", 3));
  }

  // Trạng thái không hoàn tiền — chỉ được phép cập nhật trạng thái
  @Test
  void testUpdateOrderStatus_NormalFlow_NoRefund_NoStockChange() {

    when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    OrderDTO result = orderService.updateOrderStatus("o1", 2);

    assertEquals(2, result.getStatus());
    assertEquals(10, book.getStock()); // stock không thay đổi

    try {
      verify(momoService, never()).refundPayment(anyMap());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Cập nhật đơn hàng thành status 4 hoặc 5 và đơn hàng thanh toán bằng Momo thì
  // hoàn tiền Momo và trả lại số lượng hiện có
  @Test
  void testUpdateOrderStatus_RefundAndReturnStock() throws Exception {

    order.setPaymethod("momo");

    when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
    when(paymentRepository.findFirstByOrder_OrderCode("ORD123")).thenReturn(Optional.of(payment));
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // mock momo refund
    Mockito.doNothing().when(momoService).refundPayment(any());

    OrderDTO result = orderService.updateOrderStatus("o1", 4);

    // stock: 10 + 3 = 13
    assertEquals(13, book.getStock());

    verify(momoService).refundPayment(anyMap());
    assertEquals(4, result.getStatus());
  }

  // Phương thức thanh toán không phải Momo thì chỉ trả lại số lượng hiện có,
  // không hoàn tiền
  @Test
  void testUpdateOrderStatus_ReturnStock_NoRefundWhenNotMomo() {

    order.setPaymethod("cod");

    when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    orderService.updateOrderStatus("o1", 5);

    assertEquals(13, book.getStock());
    try {
      verify(momoService, never()).refundPayment(any());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // Hoàn tiền Momo thất bại thông báo lỗi
  @Test
  void testUpdateOrderStatus_RefundFails_ThrowsException() throws Exception {

    order.setPaymethod("momo");

    when(orderRepository.findById("o1")).thenReturn(Optional.of(order));
    when(paymentRepository.findFirstByOrder_OrderCode("ORD123")).thenReturn(Optional.of(payment));

    doThrow(new RuntimeException("Refund failed")).when(momoService).refundPayment(anyMap());

    assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus("o1", 4));
  }
}
