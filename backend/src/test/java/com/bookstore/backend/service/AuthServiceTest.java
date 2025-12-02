package com.bookstore.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bookstore.backend.dto.LoginRequest;
import com.bookstore.backend.dto.LoginResponse;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.JwtUtils;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtUtils jwtUtils;

  @InjectMocks private AuthService authService;

  private User user;
  private LoginRequest request;

  @BeforeEach
  void setup() {
    request = new LoginRequest("test@example.com", "123456");

    user =
        User.builder()
            .id("u1")
            .email("test@example.com")
            .fullname("John Test")
            .password("$2a$10$encrypted")
            .role(3)
            .status(1)
            .build();
  }

  // Đăng nhập thành công
  @Test
  void testLogin_Success() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("123456", "$2a$10$encrypted")).thenReturn(true);
    when(jwtUtils.generateToken(user)).thenReturn("fake-jwt-token");

    LoginResponse response = authService.login(request);

    assertEquals("fake-jwt-token", response.getToken());
    assertEquals("u1", response.getId());
    verify(jwtUtils).generateToken(user);
  }

  // Email không tồn tại
  @Test
  void testLogin_EmailNotFound() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> authService.login(request));
  }

  // Tài khoản đã bị chặn
  @Test
  void testLogin_UserLocked() {
    user.setStatus(0);
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    assertThrows(IllegalArgumentException.class, () -> authService.login(request));
  }

  // Sai mật khẩu
  @Test
  void testLogin_WrongPassword() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("123456", "$2a$10$encrypted")).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> authService.login(request));
  }

  // Lấy thông tin tài khoản từ token
  @Test
  void testGetUserFromToken() {
    Map<String, Object> mockData = Map.of("id", "u1", "email", "test@example.com");

    when(jwtUtils.getUserFromToken("token123")).thenReturn(mockData);

    Map<String, Object> result = authService.getUserFromToken("token123");

    assertEquals("u1", result.get("id"));
    verify(jwtUtils).getUserFromToken("token123");
  }
}
