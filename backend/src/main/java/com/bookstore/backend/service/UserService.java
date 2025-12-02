package com.bookstore.backend.service;

import com.bookstore.backend.dto.UserDTO;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.AddressRepository;
import com.bookstore.backend.repository.CartRepository;
import com.bookstore.backend.repository.OrderRepository;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.ValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UserRepository userRepository,
      AddressRepository addressRepository,
      OrderRepository orderRepository,
      CartRepository cartRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.addressRepository = addressRepository;
    this.orderRepository = orderRepository;
    this.cartRepository = cartRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // lấy tất cả users có role = 3 là customer
  public Page<UserDTO> getAllCustomers(int page, int limit, String q, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    List<Integer> roles = List.of(3);

    Page<User> customersPage;

    if (q != null && !q.isEmpty() && status != null) {
      customersPage =
          userRepository.findByEmailContainingIgnoreCaseAndRoleInAndStatus(
              q, roles, status, pageable);
    } else if (q != null && !q.isEmpty()) {
      customersPage = userRepository.findByEmailContainingIgnoreCaseAndRoleIn(q, roles, pageable);
    } else if (status != null) {
      customersPage = userRepository.findByRoleInAndStatus(roles, status, pageable);
    } else {
      customersPage = userRepository.findByRoleIn(roles, pageable);
    }

    List<UserDTO> dtos =
        customersPage.getContent().stream()
            .map(
                user ->
                    UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .fullname(user.getFullname())
                        .build())
            .toList();

    return new PageImpl<>(dtos, pageable, customersPage.getTotalElements());
  }

  // lấy tất cả user có role là 0,1,2 là admin hoặc nhân viên
  public Page<UserDTO> getAllAdmins(int page, int limit, String q, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    List<Integer> roles = List.of(0, 1, 2);

    Page<User> adminsPage;

    if (q != null && !q.isEmpty() && status != null) {
      adminsPage =
          userRepository.findByEmailContainingIgnoreCaseAndRoleInAndStatus(
              q, roles, status, pageable);
    } else if (q != null && !q.isEmpty()) {
      adminsPage = userRepository.findByEmailContainingIgnoreCaseAndRoleIn(q, roles, pageable);
    } else if (status != null) {
      adminsPage = userRepository.findByRoleInAndStatus(roles, status, pageable);
    } else {
      adminsPage = userRepository.findByRoleIn(roles, pageable);
    }

    List<UserDTO> dtos =
        adminsPage.getContent().stream()
            .map(
                user ->
                    UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .fullname(user.getFullname())
                        .build())
            .toList();

    return new PageImpl<>(dtos, pageable, adminsPage.getTotalElements());
  }

  // lấy 1 user theo id
  public Optional<UserDTO> getUserById(String id) {
    return userRepository
        .findById(id)
        .map(
            user ->
                UserDTO.builder()
                    .id(user.getId())
                    .fullname(user.getFullname())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .build());
  }

  // tạo user
  public UserDTO createUser(UserDTO dto) {
    if (!ValidationUtils.validateEmail(dto.getEmail())) {
      throw new IllegalArgumentException("Email không hợp lệ");
    }

    if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Email đã tồn tại");
    }

    if (dto.getPassword() == null || dto.getPassword().length() < 6) {
      throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
    }

    User user =
        User.builder()
            .email(dto.getEmail())
            .fullname(dto.getFullname())
            .password(passwordEncoder.encode(dto.getPassword()))
            .status(dto.getStatus() != null ? dto.getStatus() : 1)
            .role(dto.getRole() != null ? dto.getRole() : 3)
            .build();

    User saved = userRepository.save(user);

    dto.setId(saved.getId());
    dto.setPassword(null);
    return dto;
  }

  // cập nhật user
  public UserDTO updateUser(String id, UserDTO userDTO) {
    return userRepository
        .findById(id)
        .map(
            user -> {
              if (!ValidationUtils.validateEmail(userDTO.getEmail())) {
                throw new IllegalArgumentException("Email không hợp lệ");
              }

              if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email đã tồn tại");
              }

              if (userDTO.getEmail() != null) {
                user.setEmail(userDTO.getEmail());
              }
              if (userDTO.getFullname() != null) {
                user.setFullname(userDTO.getFullname());
              }
              if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                if (userDTO.getPassword().length() < 6) {
                  throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
                }
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
              }
              if (userDTO.getRole() != null) {
                user.setRole(userDTO.getRole());
              }
              if (userDTO.getStatus() != null) {
                user.setStatus(userDTO.getStatus());
              }
              userRepository.save(user);

              userDTO.setId(user.getId());
              userDTO.setPassword(null);
              return userDTO;
            })
        .orElse(null);
  }

  // cập nhật status của user
  public User updateUserStatus(String id, Integer status) {
    return userRepository
        .findById(id)
        .map(
            user -> {
              user.setStatus(status);
              return userRepository.save(user);
            })
        .orElseThrow(() -> new EntityNotFoundException("Người dùng không tìm thấy"));
  }

  // Xóa user
  public void deleteUser(String id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Người dùng không tìm thấy"));

    if (orderRepository.existsByUser(user)) {
      throw new IllegalStateException("Người dùng này không thể xóa vì đã mua hàng");
    }

    // xóa tất cả address và cart trước
    addressRepository.deleteByUserId(user.getId());
    cartRepository.deleteByUserId(user.getId());
    userRepository.deleteById(id);
  }
}
