package com.bookstore.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String id;
    private String title;
    private double price;
    private double discount;
    private String slug;
    private int stock;
    private int status;
    private String createdAt;
    private AuthorDTO author;
    private PublisherDTO publisher;
    private CategoryDTO category;

    private List<ImageBookDTO> images;
}
