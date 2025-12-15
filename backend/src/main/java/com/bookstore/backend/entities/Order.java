package com.bookstore.backend.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @NotBlank(message = "Mã đơn không được để trống")
  @Column(nullable = false, unique = true, length = 15)
  private String orderCode;

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

  @NotBlank(message = "Tỉnh/thành phố không được để trống")
  @Column(nullable = false, length = 70)
  private String city;

  @NotBlank(message = "Phường/xã không được để trống")
  @Column(nullable = false, length = 70)
  private String ward;

  @NotBlank(message = "Phương thức thanh toán không được để trống")
  @Column(nullable = false, length = 15)
  private String paymethod;

  @NotNull
  @Column(nullable = false)
  private Integer status;

  @NotNull
  @Column(nullable = false)
  private Double total;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @NotNull
  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderDetail> items;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Payment> payments;
}
