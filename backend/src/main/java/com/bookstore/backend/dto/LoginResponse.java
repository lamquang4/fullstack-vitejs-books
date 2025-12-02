package com.bookstore.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
  private String token;
  private String id;
  private String email;
  private String fullname;
  private Integer role; // 0,1,2 = admin ; 3 = customer
}
