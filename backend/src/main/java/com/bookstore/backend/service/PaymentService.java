package com.bookstore.backend.service;

import com.bookstore.backend.dto.PaymentDTO;
import com.bookstore.backend.entities.Payment;
import com.bookstore.backend.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  // lấy tất cả các payments
  public Page<PaymentDTO> getAllPayments(int page, int limit, String q, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    Page<Payment> paymentPage;

    if ((q != null && !q.isEmpty()) && status != null) {
      paymentPage =
          paymentRepository.findByOrder_OrderCodeContainingIgnoreCaseAndStatus(q, status, pageable);
    } else if (q != null && !q.isEmpty()) {
      paymentPage = paymentRepository.findByOrder_OrderCodeContainingIgnoreCase(q, pageable);
    } else if (status != null) {
      paymentPage = paymentRepository.findByStatus(status, pageable);
    } else {
      paymentPage = paymentRepository.findAll(pageable);
    }
    return paymentPage.map(this::convertToDTO);
  }

  public Payment createPayment(Payment payment) {
    return paymentRepository.save(payment);
  }

  private PaymentDTO convertToDTO(Payment payment) {
    return PaymentDTO.builder()
        .id(payment.getId())
        .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
        .orderCode(payment.getOrder() != null ? payment.getOrder().getOrderCode() : null)
        .paymethod(payment.getPaymethod())
        .amount(payment.getAmount())
        .transactionId(payment.getTransactionId())
        .status(payment.getStatus())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}
