package com.bookstore.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BookDTO {
    private String id;
    private String title;
    private List<String> images; 
    private Double price;
    private Double discount;
    private String slug;
    private String authorName;
    private String publisherName;
    private String categoryName;
    private Integer stock;
    private Integer status;
}
