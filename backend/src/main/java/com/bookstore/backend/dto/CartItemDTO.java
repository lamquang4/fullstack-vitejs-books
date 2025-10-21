package com.bookstore.backend.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
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
