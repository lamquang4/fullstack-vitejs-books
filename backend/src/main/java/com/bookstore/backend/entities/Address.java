package com.bookstore.backend.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, length = 50)
  private String fullname;

  @Column(nullable = false, length = 15)
  private String phone;

  @Column(nullable = false, length = 70)
  private String speaddress;

  @Column(nullable = false, length = 70)
  private String ward;

  @Column(nullable = false, length = 70)
  private String city;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
