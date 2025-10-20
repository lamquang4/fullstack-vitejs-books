package com.bookstore.backend.service;
import org.springframework.stereotype.Service;
import com.bookstore.backend.repository.CartRepository;


@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;

    }

}
