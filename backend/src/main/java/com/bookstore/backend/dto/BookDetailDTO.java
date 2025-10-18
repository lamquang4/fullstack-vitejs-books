package com.bookstore.backend.dto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class BookDetailDTO {
    private String id;
    private String title;
    private List<String> images; 
    private Double price;
    private Double discount;
    private String description;
    private String slug;
    private String authorName;
    private String categoryName;
    private Integer numberOfPages;
    private Double weight;
    private Double width;      
    private Double length;    
    private Double thickness; 
    private Integer stock;
    private Integer status;
}
