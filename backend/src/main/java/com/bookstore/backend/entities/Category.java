package com.bookstore.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotBlank(message = "Tên danh mục không được để trống")
  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Column(nullable = false, unique = true, length = 50)
  private String slug;

  @NotNull
  @Column(nullable = false)
  private Integer status;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
