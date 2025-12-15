package com.bookstore.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "author")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotBlank(message = "Họ tên tác giả không được để trống")
  @Column(nullable = false, unique = true, length = 50)
  private String fullname;

  @NotBlank(message = "Slug không được để trống")
  @Column(nullable = false, unique = true, length = 50)
  private String slug;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
