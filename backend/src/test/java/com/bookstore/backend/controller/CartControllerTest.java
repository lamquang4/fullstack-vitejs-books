package com.bookstore.backend.controller;

import com.bookstore.backend.dto.CartDTO;
import com.bookstore.backend.service.CartService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CartService cartService;

        // ---------------- GET CART ----------------
        @Test
        @WithMockUser
        void getCartByUserId_shouldReturnCart() throws Exception {

                CartDTO mockCart = new CartDTO();
                Mockito.when(cartService.getCartByUserId("u1"))
                                .thenReturn(mockCart);

                mockMvc.perform(
                                get("/api/cart/{userId}", "u1"))
                                .andExpect(status().isOk());
        }

        // Thêm sản phẩm vào giỏ hàng
        @Test
        @WithMockUser
        void addItemToCart_shouldReturnOk() throws Exception {

                Mockito.doNothing()
                                .when(cartService)
                                .addItemToCart("u1", "b1", 2);

                mockMvc.perform(
                                post("/api/cart")
                                                .param("userId", "u1")
                                                .param("bookId", "b1")
                                                .param("quantity", "2")
                                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        // Cập nhật số lượng mua trong giỏ hàng
        @Test
        @WithMockUser
        void updateCartItemQuantity_shouldReturnOk() throws Exception {

                Mockito.doNothing()
                                .when(cartService)
                                .updateCartItemQuantity("ci1", 5);

                mockMvc.perform(
                                put("/api/cart/item/{cartItemId}", "ci1")
                                                .param("quantity", "5")
                                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        // Xóa sản phẩm khỏi giỏ hàng
        @Test
        @WithMockUser
        void removeItemFromCart_shouldReturnOk() throws Exception {

                Mockito.doNothing()
                                .when(cartService)
                                .removeItemFromCart("ci1");

                mockMvc.perform(
                                delete("/api/cart/item/{cartItemId}", "ci1")
                                                .with(csrf()))
                                .andExpect(status().isOk());
        }

}
