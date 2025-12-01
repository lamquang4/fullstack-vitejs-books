package com.bookstore.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class BookDetailDTO {
    private String id;
    private String title;
    private Double price;
    private Double discount;
    private String description;
    private String slug;
    private Integer numberOfPages;
    private String publicationDate;
    private Double weight;
    private Double width;      
    private Double length;    
    private Double thickness; 
    private Integer stock;
    private Integer status;
    private String createdAt;
    private AuthorDTO author;
    private PublisherDTO publisher;
    private CategoryDTO category;

    private List<ImageBookDTO> images;
}
