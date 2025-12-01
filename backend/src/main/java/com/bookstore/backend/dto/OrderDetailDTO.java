package com.bookstore.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private String bookId;
    private String title; 
    private List<String> images;
    private Integer quantity; // số lượng mua
    private Double price; // giá book tại thời điểm đặt đơn
    private Double discount; // giá giảm tại thời điểm đặt đơn
}
