package com.bookstore.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomoPaymentResponse {
    private String payUrl;
    private String requestId;
    private String orderId;
    private String errorCode;
    private String message;
}
