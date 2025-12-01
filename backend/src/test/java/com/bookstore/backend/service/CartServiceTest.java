package com.bookstore.backend.service;

import com.bookstore.backend.dto.CartDTO;
import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartItemRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Book book;
    private Cart cart;
    private CartItem item;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id("u1")
                .email("a@gmail.com")
                .build();

        book = Book.builder()
                .id("b1")
                .title("Book A")
                .slug("book-a")
                .price(100.0)
                .discount(10.0)
                .stock(20)
                .images(List.of(
                        ImageBook.builder().image("img1").createdAt(LocalDateTime.now().minusDays(2)).build(),
                        ImageBook.builder().image("img2").createdAt(LocalDateTime.now().minusDays(1)).build()
                ))
                .build();

        item = CartItem.builder()
                .id("ci1")
                .book(book)
                .quantity(3)
                .build();

        cart = Cart.builder()
                .id("c1")
                .user(user)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>(List.of(item)))
                .build();

        item.setCart(cart);
    }

 
    // Lấy giỏ hàng của khách hàng
    @Test
    void testGetCartByUserId_Success() {
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));

        CartDTO result = cartService.getCartByUserId("u1");

        assertEquals("c1", result.getId());
        assertEquals(1, result.getItems().size());
        assertEquals("b1", result.getItems().get(0).getBookId());

        verify(cartRepository).findByUserId("u1");
    }

    @Test
    void testGetCartByUserId_NotFound() {
        when(cartRepository.findByUserId("u404")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> cartService.getCartByUserId("u404"));
    }



    // Thêm sản phẩm vào giỏ hàng
    @Test
    void testAddItemToCart_UserNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> cartService.addItemToCart("u1", "b1", 2));
    }

    @Test
    void testAddItemToCart_BookNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> cartService.addItemToCart("u1", "b1", 2));
    }

    @Test
    void testAddItemToCart_InvalidQuantity() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));

        assertThrows(IllegalArgumentException.class,
                () -> cartService.addItemToCart("u1", "b1", 0));
    }

    @Test
    void testAddItemToCart_CreateNewCart() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            c.setId("newCart");
            return c;
        });

        assertDoesNotThrow(() -> cartService.addItemToCart("u1", "b1", 2));
        verify(cartItemRepository).save(any());
    }

    @Test
    void testAddItemToCart_AddNewItem() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId("c1", "b1"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> cartService.addItemToCart("u1", "b1", 5));
        verify(cartItemRepository).save(any());
    }

    @Test
    void testAddItemToCart_UpdateExistingItem() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId("c1", "b1"))
                .thenReturn(Optional.of(item));

        assertDoesNotThrow(() -> cartService.addItemToCart("u1", "b1", 2));
        verify(cartItemRepository).save(any());
        assertEquals(5, item.getQuantity());
    }

    @Test
    void testAddItemToCart_ExceedStock() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId("c1", "b1"))
                .thenReturn(Optional.of(item));

        item.setQuantity(19);
        assertThrows(IllegalStateException.class,
                () -> cartService.addItemToCart("u1", "b1", 5));
    }

    @Test
    void testAddItemToCart_ExceedMaxLimit() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(bookRepository.findById("b1")).thenReturn(Optional.of(book));
        when(cartRepository.findByUserId("u1")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndBookId("c1", "b1"))
                .thenReturn(Optional.of(item));

        item.setQuantity(10);  // ⬅ quantity ban đầu
        book.setStock(20);     // ⬅ đảm bảo không bị vượt stock

        assertDoesNotThrow(() -> cartService.addItemToCart("u1", "b1", 10));
        assertEquals(15, item.getQuantity()); // max limit
    }


    // Cập nhật giỏ hàng
    @Test
    void testUpdateItem_NotFound() {
        when(cartItemRepository.findById("ci404")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> cartService.updateCartItemQuantity("ci404", 5));
    }

    @Test
    void testUpdateItem_RemoveWhenQuantityZero() {
        when(cartItemRepository.findById("ci1")).thenReturn(Optional.of(item));
        assertDoesNotThrow(() -> cartService.updateCartItemQuantity("ci1", 0));
        verify(cartItemRepository).delete(item);
    }

    @Test
    void testUpdateItem_ExceedStock() {
        when(cartItemRepository.findById("ci1")).thenReturn(Optional.of(item));
        item.getBook().setStock(5);

        assertDoesNotThrow(() -> cartService.updateCartItemQuantity("ci1", 10));
        assertEquals(5, item.getQuantity());
        verify(cartItemRepository).save(item);
    }

    @Test
    void testUpdateItem_ExceedMaxLimit() {
        when(cartItemRepository.findById("ci1")).thenReturn(Optional.of(item));
        assertDoesNotThrow(() -> cartService.updateCartItemQuantity("ci1", 50));
        assertEquals(15, item.getQuantity());
    }

    @Test
    void testUpdateItem_NormalUpdate() {
        when(cartItemRepository.findById("ci1")).thenReturn(Optional.of(item));
        assertDoesNotThrow(() -> cartService.updateCartItemQuantity("ci1", 7));
        assertEquals(7, item.getQuantity());
        verify(cartItemRepository).save(item);
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @Test
    void testRemoveItem_NotFound() {
        when(cartItemRepository.findById("ci404")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> cartService.removeItemFromCart("ci404"));
    }

    @Test
    void testRemoveItem_Success() {
        when(cartItemRepository.findById("ci1")).thenReturn(Optional.of(item));
        assertDoesNotThrow(() -> cartService.removeItemFromCart("ci1"));
        verify(cartItemRepository).delete(item);
    }
}
