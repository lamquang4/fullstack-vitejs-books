package com.bookstore.backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.dto.LoginRequest;
import com.bookstore.backend.dto.LoginResponse;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.repository.UserRepository;
import com.bookstore.backend.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private JwtUtils jwtUtils;

  @Autowired private ObjectMapper objectMapper;

  private User user;

  @BeforeEach
  void setup() {
    userRepository.deleteAll();

    user =
        User.builder()
            .email("user@gmail.com")
            .fullname("Nguyen Van A")
            .password(passwordEncoder.encode("123456"))
            .role(3)
            .status(1)
            .createdAt(LocalDateTime.now())
            .build();

    userRepository.save(user);
  }

  // Đăng nhập thành công
  @Test
  void testLoginSuccess() throws Exception {
    LoginRequest request = new LoginRequest("user@gmail.com", "123456");

    String responseJson =
        mockMvc
            .perform(
                post("/api/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email").value("user@gmail.com"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    LoginResponse response = objectMapper.readValue(responseJson, LoginResponse.class);

    assertNotNull(response.getToken());
    assertEquals(user.getId(), response.getId());
  }

  // Đăng nhập - sai mật khẩu
  @Test
  void testLoginWrongPassword() throws Exception {
    LoginRequest request = new LoginRequest("user@gmail.com", "wrong-password");

    mockMvc
        .perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // Đăng nhập - email không tìm thấy
  @Test
  void testLoginEmailNotFound() throws Exception {
    LoginRequest request = new LoginRequest("notfound@gmail.com", "123456");

    mockMvc
        .perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // Đăng nhập - tài khoản bị khóa
  @Test
  void testLoginUserLocked() throws Exception {
    user.setStatus(0);
    userRepository.save(user);

    LoginRequest request = new LoginRequest("user@gmail.com", "123456");

    mockMvc
        .perform(
            post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // /me WITHOUT TOKEN → 401
  @Test
  void testGetMeWithoutToken() throws Exception {
    mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
  }

  // /me WITH VALID TOKEN
  @Test
  void testGetMeWithValidToken() throws Exception {
    String jwt = jwtUtils.generateToken(user);

    mockMvc
        .perform(get("/api/auth/me").header("Authorization", "Bearer " + jwt))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("user@gmail.com"))
        .andExpect(jsonPath("$.fullname").value("Nguyen Van A"))
        .andExpect(jsonPath("$.role").value(3));
  }
}
