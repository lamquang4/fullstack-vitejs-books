package com.bookstore.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.bookstore.backend.dto.CartDTO;
import com.bookstore.backend.dto.CartItemDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Cart;
import com.bookstore.backend.entities.CartItem;
import com.bookstore.backend.entities.ImageBook;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartItemRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.Comparator;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final int max = 15;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            BookRepository bookRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // lấy giỏ hàng của customer dựa vào user id
    public CartDTO getCartByUserId(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Giỏ hàng không tìm thấy"));

        List<CartItemDTO> itemDTOs = cart.getItems().stream().map(item -> CartItemDTO.builder()
                .id(item.getId())
                .bookId(item.getBook().getId())
                .title(item.getBook().getTitle())
                .slug(item.getBook().getSlug())
                .images(
                        item.getBook().getImages()
                                .stream()
                                .sorted(Comparator.comparing(ImageBook::getCreatedAt))
                                .map(ImageBook::getImage)
                                .collect(Collectors.toList()))
                .price(item.getBook().getPrice())
                .discount(item.getBook().getDiscount())
                .stock(item.getBook().getStock())
                .quantity(item.getQuantity())
                .build()).toList();

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .createdAt(cart.getCreatedAt())
                .items(itemDTOs)
                .build();
    }

    // thêm sản phẩm vào giỏ hàng
    @Transactional
    public void addItemToCart(String userId, String bookId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Khách hàng không tìm thấy"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Sách không tìm thấy"));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng mua phải lớn hơn 0");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + quantity;

            // Nếu vượt quá tồn kho, giới hạn bằng tồn kho
            if (newQuantity > book.getStock()) {
                throw new IllegalStateException("Số lượng hiện có không đủ");
            }

            // Nếu vượt quá max, giới hạn bằng max
            if (newQuantity > max) {
                newQuantity = max;
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {

            int finalQuantity = Math.min(quantity, Math.min(book.getStock(), max));

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .book(book)
                    .quantity(finalQuantity)
                    .build();

            cartItemRepository.save(newItem);
        }
    }

    // cập nhật số lượng mua sản phẩm trong cart item
    @Transactional
    public void updateCartItemQuantity(String cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Sản phẩm trong giỏ hàng không tìm thấy"));

        Book book = item.getBook();

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return;
        }

        if (quantity > book.getStock()) {
            quantity = book.getStock();
        }

        if (quantity > max) {
            quantity = max;
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    // xóa sản phẩm cart item khỏi cart
    @Transactional
    public void removeItemFromCart(String cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy"));
        cartItemRepository.delete(item);
    }

}
