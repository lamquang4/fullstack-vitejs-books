package com.bookstore.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "`user`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotBlank(message = "Email không được để trống")
  @Email(message = "Email không đúng định dạng")
  @Column(unique = true, nullable = false, length = 100)
  private String email;

  @NotBlank(message = "Họ tên không được để trống")
  @Column(nullable = false, length = 50)
  private String fullname;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Column(nullable = false)
  private String password;

  @NotNull
  @Column(nullable = false)
  private Integer role;

  @NotNull
  @Column(nullable = false)
  private Integer status;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
}
