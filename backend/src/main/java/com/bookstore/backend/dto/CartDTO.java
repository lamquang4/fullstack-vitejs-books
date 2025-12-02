package com.bookstore.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
  private String id;
  private String userId;
  private LocalDateTime createdAt;
  private List<CartItemDTO> items;
}
