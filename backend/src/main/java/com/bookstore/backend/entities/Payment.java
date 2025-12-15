package com.bookstore.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// payment này lưu những thanh toán thành công, hoàn tiền của momo, vnpay...
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  private Order order;

  @NotBlank(message = "Cổng thanh toán không được để trống")
  @Column(nullable = false, length = 20)
  private String paymethod;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Double amount;

  @NotBlank(message = "Mã giao dịch không được để trống")
  @Column(length = 100)
  private String transactionId;

  @NotNull
  @Column(length = 20)
  private Integer status; // 1 thành công, 0 hoàn tiền

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
