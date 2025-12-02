package com.bookstore.backend.service;

import com.bookstore.backend.dto.PaymentDTO;
import com.bookstore.backend.entities.Order;
import com.bookstore.backend.entities.Payment;
import com.bookstore.backend.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PaymentServiceTest {

        @Mock
        private PaymentRepository paymentRepository;

        @InjectMocks
        private PaymentService paymentService;

        private Payment payment;
        private Order order;

        @BeforeEach
        void setup() {
                order = Order.builder()
                                .id("order1")
                                .orderCode("ABC123")
                                .build();

                payment = Payment.builder()
                                .id("pay1")
                                .order(order)
                                .paymethod("momo")
                                .amount(100.0)
                                .transactionId("tx123")
                                .status(1)
                                .createdAt(LocalDateTime.now())
                                .build();
        }

        // Lấy tất cả giao dịch thanh toán
        @Test
        void testGetAllPayments_FilterByOrderCodeAndStatus() {
                Page<Payment> page = new PageImpl<>(List.of(payment));

                when(paymentRepository
                                .findByOrder_OrderCodeContainingIgnoreCaseAndStatus("ABC", 1,
                                                PageRequest.of(0, 10, Sort.by("createdAt").descending())))
                                .thenReturn(page);

                Page<PaymentDTO> result = paymentService.getAllPayments(1, 10, "ABC", 1);

                assertEquals(1, result.getTotalElements());
                assertEquals("pay1", result.getContent().get(0).getId());
        }

        @Test
        void testGetAllPayments_FilterByOrderCodeOnly() {
                Page<Payment> page = new PageImpl<>(List.of(payment));

                when(paymentRepository
                                .findByOrder_OrderCodeContainingIgnoreCase("ABC",
                                                PageRequest.of(0, 10, Sort.by("createdAt").descending())))
                                .thenReturn(page);

                Page<PaymentDTO> result = paymentService.getAllPayments(1, 10, "ABC", null);

                assertEquals(1, result.getTotalElements());
                verify(paymentRepository)
                                .findByOrder_OrderCodeContainingIgnoreCase(eq("ABC"), any(Pageable.class));
        }

        @Test
        void testGetAllPayments_FilterByStatusOnly() {
                Page<Payment> page = new PageImpl<>(List.of(payment));

                when(paymentRepository.findByStatus(
                                eq(1),
                                any(Pageable.class)))
                                .thenReturn(page);

                Page<PaymentDTO> result = paymentService.getAllPayments(1, 10, null, 1);

                assertEquals(1, result.getTotalElements());
                verify(paymentRepository)
                                .findByStatus(eq(1), any(Pageable.class));
        }

        @Test
        void testGetAllPayments_NoFilter() {
                Page<Payment> page = new PageImpl<>(List.of(payment));

                when(paymentRepository.findAll(any(Pageable.class)))
                                .thenReturn(page);

                Page<PaymentDTO> result = paymentService.getAllPayments(1, 10, null, null);

                assertEquals(1, result.getTotalElements());
                verify(paymentRepository).findAll(any(Pageable.class));
        }

        // Lưu giao dịch thanh toán
        @Test
        void testCreatePayment_Success() {
                when(paymentRepository.save(payment)).thenReturn(payment);

                Payment saved = paymentService.createPayment(payment);

                assertNotNull(saved);
                assertEquals("pay1", saved.getId());
                verify(paymentRepository).save(payment);
        }
}
