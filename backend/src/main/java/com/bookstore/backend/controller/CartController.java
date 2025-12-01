package com.bookstore.backend.controller;
import com.bookstore.backend.dto.CartDTO;
import com.bookstore.backend.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

       @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable String userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

       @PostMapping
    public ResponseEntity<Void> addItemToCart(
            @RequestParam String userId,
            @RequestParam String bookId,
            @RequestParam int quantity) {
        cartService.addItemToCart(userId, bookId, quantity);
        return ResponseEntity.ok().build();
    }

    // items trong cart
    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable String cartItemId,
            @RequestParam int quantity) {
        cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable String cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok().build();
    }
  
}
