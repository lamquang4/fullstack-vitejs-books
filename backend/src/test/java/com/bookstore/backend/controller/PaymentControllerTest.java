package com.bookstore.backend.controller;

import com.bookstore.backend.dto.PaymentDTO;
import com.bookstore.backend.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentDTO paymentDTO;

    @BeforeEach
    void setup() {
        paymentDTO = PaymentDTO.builder()
                .id("pay1")
                .orderId("ord1")
                .orderCode("ORD001")
                .paymethod("MOMO")
                .amount(150000.0)
                .transactionId("TRANS123")
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Lấy tất cả giao dịch thanh toán
    @Test
    @WithMockUser
    void getAllPayments_shouldReturnPayments() throws Exception {

        Page<PaymentDTO> page =
                new PageImpl<>(List.of(paymentDTO));

        Mockito.when(paymentService.getAllPayments(
                        1, 12, null, null))
                .thenReturn(page);

        mockMvc.perform(
                        get("/api/payment")
                                .param("page","1")
                                .param("limit","12")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments").isArray())
                .andExpect(jsonPath("$.payments[0].paymethod").value("MOMO"))
                .andExpect(jsonPath("$.payments[0].orderCode").value("ORD001"))
                .andExpect(jsonPath("$.payments[0].transactionId").value("TRANS123"))
                .andExpect(jsonPath("$.payments[0].amount").value(150000.0))
                .andExpect(jsonPath("$.payments[0].status").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.total").value(1));
    }

    // Lấy tất cả thanh toán nhưng trong trường hợp không có thanh toán nào
    @Test
    @WithMockUser
    void getAllPayments_emptyResult() throws Exception {

        Page<PaymentDTO> page =
                new PageImpl<>(List.of());

        Mockito.when(paymentService.getAllPayments(
                        1, 12, null, null))
                .thenReturn(page);

        mockMvc.perform(
                        get("/api/payment")
                                .param("page","1")
                                .param("limit","12")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments").isEmpty())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.total").value(0));
    }
}
