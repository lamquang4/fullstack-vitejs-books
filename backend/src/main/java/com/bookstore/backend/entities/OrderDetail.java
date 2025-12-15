package com.bookstore.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orderdetail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Integer quantity;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Double price;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private Double discount;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "bookId", nullable = false)
  private Book book;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "orderId", nullable = false)
  private Order order;
}
