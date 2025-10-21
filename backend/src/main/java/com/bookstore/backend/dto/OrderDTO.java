package com.bookstore.backend.dto;
import java.util.List;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private String id;
    private String orderCode;
    private String fullname;
    private String phone;
    private String speaddress;
    private String city;
    private String ward;
    private String paymethod;
    private Integer status;
    private Double total;
    private String userFullname;
    private LocalDateTime createdAt;
    private List<OrderDetailDTO> items;
}
