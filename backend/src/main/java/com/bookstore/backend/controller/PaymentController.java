package com.bookstore.backend.controller;

import com.bookstore.backend.dto.PaymentDTO;
import com.bookstore.backend.service.PaymentService;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @GetMapping
  public ResponseEntity<?> getAllPayments(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "12") int limit,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Integer status) {
    Page<PaymentDTO> paymentPage = paymentService.getAllPayments(page, limit, q, status);

    return ResponseEntity.ok(
        Map.of(
            "payments", paymentPage.getContent(),
            "totalPages", paymentPage.getTotalPages(),
            "total", paymentPage.getTotalElements()));
  }
}
