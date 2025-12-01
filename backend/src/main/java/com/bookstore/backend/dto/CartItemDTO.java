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
public class CartItemDTO {
    private String id;
    private String bookId;
    private String title;
    private String slug;
    private List<String> images;
    private double price;
    private double discount;
    private int stock;
    private int quantity;
}
