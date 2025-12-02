package com.bookstore.backend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private String id;
    private String orderId;
    private String orderCode;
    private String paymethod;
    private Double amount;
    private String transactionId;
    private Integer status;
    private LocalDateTime createdAt;
}
