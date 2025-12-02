package com.bookstore.backend.repository;

import com.bookstore.backend.entities.Payment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
  Page<Payment> findByOrder_OrderCodeContainingIgnoreCase(String orderCode, Pageable pageable);

  Page<Payment> findByStatus(Integer status, Pageable pageable);

  Page<Payment> findByOrder_OrderCodeContainingIgnoreCaseAndStatus(
      String orderCode, Integer status, Pageable pageable);

  Optional<Payment> findFirstByOrder_OrderCode(String orderCode);
}
