package com.bookstore.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private long totalSold;
  private List<ImageBookDTO> images;
}
