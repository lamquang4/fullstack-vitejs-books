package com.bookstore.backend.service;

import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.dto.OrderDetailDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.*;
import com.bookstore.backend.utils.ValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderService_CreateOrderTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private MomoService momoService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Book book;
    private OrderDTO orderDTO;
    private OrderDetailDTO detailDTO;

    @BeforeEach
    void setup() {

        user = User.builder()
                .id("u1")
                .email("test@gmail.com")
                .build();

        book = Book.builder()
                .id("b1")
                .title("Java Book")
                .price(100.0)
                .discount(10.0)
                .stock(50)
                .build();

        detailDTO = new OrderDetailDTO();
        detailDTO.setBookId("b1");
        detailDTO.setQuantity(2);
        detailDTO.setPrice(100.0);
        detailDTO.setDiscount(10.0);

        orderDTO = new OrderDTO();
        orderDTO.setFullname("Nguyen Van A");
        orderDTO.setPhone("0123456789");
        orderDTO.setSpeaddress("123A");
        orderDTO.setCity("HCM");
        orderDTO.setWard("1");
        orderDTO.setPaymethod("cod");
        orderDTO.setItems(List.of(detailDTO));
    }

    // Người dùng không tìm thấy
    @Test
    void testCreateOrder_UserNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.createOrder(orderDTO, "u1"));
    }

    // Số điện thoại không hợp lệ
    @Test
    void testCreateOrder_InvalidPhone() {
        orderDTO.setPhone("abc123");

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        try (MockedStatic<ValidationUtils> mocked = Mockito.mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.validatePhone("abc123")).thenReturn(false);

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.createOrder(orderDTO, "u1"));
        }
    }

    // Sách không tìm thấy
    @Test
    void testCreateOrder_BookNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.empty());

        try (MockedStatic<ValidationUtils> mocked = Mockito.mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.validatePhone(anyString())).thenReturn(true);

            assertThrows(EntityNotFoundException.class,
                    () -> orderService.createOrder(orderDTO, "u1"));
        }
    }

    // Kiểm tra số lượng mua > số lượng hiện có
    @Test
    void testCreateOrder_QuantityExceedsStock() {
        detailDTO.setQuantity(999); // vượt stock

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));

        try (MockedStatic<ValidationUtils> mocked = Mockito.mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.validatePhone(anyString())).thenReturn(true);

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.createOrder(orderDTO, "u1"));
        }
    }

    // Phương thức thanh toán COD - cập nhật số lượng hiện có, xóa giỏ hàng
    @Test
    void testCreateOrder_COD_DeductStock_AndDeleteCart() {

        Cart cart = Cart.builder().id("c1").user(user).build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(orderRepository.save(any())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId("order1");
            return o;
        });

        try (MockedStatic<ValidationUtils> mocked = Mockito.mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.validatePhone(anyString())).thenReturn(true);

            OrderDTO result = orderService.createOrder(orderDTO, "u1");

            assertNotNull(result);
            assertEquals("order1", result.getId());
            assertEquals(2, result.getItems().get(0).getQuantity());

            // stock giảm: 50 - 2 = 48
            assertEquals(48, book.getStock());

            verify(cartRepository).delete(cart);
        }
    }

    // Tính tông tiền
    @Test
    void testCreateOrder_CalculateTotalCorrectly() {

        // final price = (100 - 10) * 2 = 180
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.empty());
        when(orderRepository.save(any())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId("oid");
            return o;
        });

        try (MockedStatic<ValidationUtils> mocked = Mockito.mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.validatePhone(anyString())).thenReturn(true);

            OrderDTO result = orderService.createOrder(orderDTO, "u1");

            assertEquals(180.0, result.getTotal());
        }
    }
}
