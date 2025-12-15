package com.bookstore.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "publisher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publisher {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotBlank(message = "Tên nhà xuất bản không được để trống")
  @Column(unique = true, nullable = false, length = 50)
  private String name;

  @NotBlank(message = "Slug không được để trống")
  @Column(unique = true, nullable = false, length = 50)
  private String slug;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
