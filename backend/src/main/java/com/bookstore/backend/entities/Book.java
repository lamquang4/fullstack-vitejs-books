package com.bookstore.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotBlank(message = "Tiêu đề không được để trống")
  @Column(nullable = false, unique = true, length = 100)
  private String title;

  @NotNull
  @Column(nullable = false)
  private Double price;

  @NotNull
  @Column(nullable = false)
  private Double discount;

  @NotBlank(message = "Mô tả không được để trống")
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @NotNull
  @PastOrPresent(message = "Ngày xuất bản không hợp lệ")
  @Column(nullable = false)
  private LocalDate publicationDate;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Integer numberOfPages;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Double weight;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Double width;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Double length;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Double thickness;

  @NotNull
  @Column(unique = true, nullable = false)
  private String slug;

  @NotNull
  @Column(nullable = false)
  private Integer status;

  @NotNull
  @Min(value = 0)
  @Column(nullable = false)
  private Integer stock;

  @NotNull
  @ManyToOne()
  @JoinColumn(name = "categoryId", nullable = false)
  private Category category;

  @NotNull
  @ManyToOne()
  @JoinColumn(name = "authorId", nullable = false)
  private Author author;

  @NotNull
  @ManyToOne()
  @JoinColumn(name = "publisherId", nullable = false)
  private Publisher publisher;

  @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ImageBook> images;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
