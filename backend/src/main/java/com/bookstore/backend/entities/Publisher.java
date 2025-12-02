package com.bookstore.backend.entities;

import jakarta.persistence.*;
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

  @Column(unique = true, nullable = false, length = 50)
  private String name;

  @Column(unique = true, nullable = false, length = 50)
  private String slug;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
