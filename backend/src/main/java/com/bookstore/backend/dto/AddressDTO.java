package com.bookstore.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private String id;
    private String fullname;
    private String phone;
    private String speaddress;
    private String ward;
    private String city;
    private String userId;
}

