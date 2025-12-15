package com.bookstore.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "cartitem",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"cartId", "bookId"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "cartId", nullable = false)
  private Cart cart;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "bookId", nullable = false)
  private Book book;

  @NotNull
  @Min(value = 1)
  @Column(nullable = false)
  private int quantity;
}
