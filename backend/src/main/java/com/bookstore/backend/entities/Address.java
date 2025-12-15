package com.bookstore.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

  @NotBlank(message = "Họ tên không được để trống")
  @Column(nullable = false, length = 50)
  private String fullname;

  @NotBlank(message = "Số điện thoại không được để trống")
  @Pattern(
      regexp = "^(0?)(3[2-9]|5[689]|7[06-9]|8[0-689]|9[0-46-9])[0-9]{7}$",
      message = "Số điện thoại không hợp lệ")
  @Column(nullable = false, length = 15)
  private String phone;

  @NotBlank(message = "Địa chỉ cụ thể không được để trống")
  @Column(nullable = false, length = 70)
  private String speaddress;

  @NotBlank(message = "Phường/xã không được để trống")
  @Column(nullable = false, length = 70)
  private String ward;

  @NotBlank(message = "Tỉnh/thành phố không được để trống")
  @Column(nullable = false, length = 70)
  private String city;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
