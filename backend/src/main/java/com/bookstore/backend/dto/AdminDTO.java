package com.bookstore.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {
    private String id;
    private String email;
    private Integer role;
    private Integer status;
        private String password;
    private AddressDTO address;
    private String createdAt;
}
