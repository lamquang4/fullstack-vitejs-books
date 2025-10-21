package com.bookstore.backend.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private String id;
    private String userId;
    private LocalDateTime createdAt;
    private List<CartItemDTO> items;
}
