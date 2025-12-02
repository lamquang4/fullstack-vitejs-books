package com.bookstore.backend.controller;

import com.bookstore.backend.dto.MomoResponse;
import com.bookstore.backend.dto.OrderDTO;
import com.bookstore.backend.service.MomoService;
import com.bookstore.backend.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MomoController.class)
@ActiveProfiles("test")
class MomoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MomoService momoService;

        @MockBean
        private OrderService orderService;

        // Thanh toán bằng Momo
        @Test
        @WithMockUser
        void payWithMomo_success() throws Exception {

                OrderDTO order = OrderDTO.builder()
                                .orderCode("ORD123")
                                .fullname("Nguyen Van A")
                                .total(150000d)
                                .createdAt(LocalDateTime.now())
                                .build();

                MomoResponse response = MomoResponse.builder()
                                .payUrl("https://momo.test/pay")
                                .qrCodeUrl("https://momo.test/qr.png")
                                .resultCode(0)
                                .build();

                Mockito.when(orderService.getOrderByOrderCode("ORD123"))
                                .thenReturn(order);

                Mockito.when(momoService.createPayment(order))
                                .thenReturn(response);

                mockMvc.perform(
                                post("/api/payment/momo/qr/{orderCode}", "ORD123")
                                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.payUrl")
                                                .value("https://momo.test/pay"))
                                .andExpect(jsonPath("$.resultCode")
                                                .value(0));
        }

        // Xử lý thanh toán thành công
        @Test
        @WithMockUser
        void redirect_success_flow() throws Exception {

                Mockito.when(momoService.handleSuccessfulPayment(Mockito.any()))
                                .thenReturn(true);

                mockMvc.perform(
                                get("/api/payment/momo/redirect")
                                                .param("resultCode", "0")
                                                .param("orderId", "ORD123")
                                                .param("transId", "123")
                                                .param("message", "OK"))
                                .andExpect(status().isFound())
                                .andExpect(header().string(
                                                "Location",
                                                "http://localhost:5173/order-result?result=successful&&orderCode=ORD123"));
        }

        // Xử lý thanh toán thất bại
        @Test
        @WithMockUser
        void redirect_fail_flow() throws Exception {

                Mockito.when(momoService.handleSuccessfulPayment(Mockito.any()))
                                .thenReturn(false);

                mockMvc.perform(
                                get("/api/payment/momo/redirect")
                                                .param("resultCode", "0")
                                                .param("orderId", "ORD404"))
                                .andExpect(status().isFound())
                                .andExpect(header().string(
                                                "Location",
                                                "http://localhost:5173/order-result?result=fail"));
        }

        // Xử lý khi hủy thanh toán
        @Test
        @WithMockUser
        void redirect_cancel_flow() throws Exception {

                mockMvc.perform(
                                get("/api/payment/momo/redirect")
                                                .param("resultCode", "1"))
                                .andExpect(status().isFound())
                                .andExpect(header().string(
                                                "Location",
                                                "http://localhost:5173/"));
        }
}
