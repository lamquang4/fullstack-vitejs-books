package com.bookstore.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBookDTO {
    private String id;
    private String image;
    private String createdAt;
}
