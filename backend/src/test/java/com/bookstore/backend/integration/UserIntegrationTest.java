package com.bookstore.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bookstore.backend.dto.UserDTO;
import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
public class UserIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;

  @Autowired private UserRepository userRepository;
  @Autowired private AddressRepository addressRepository;
  @Autowired private CartRepository cartRepository;
  @Autowired private OrderRepository orderRepository;

  private User user1;
  private User user2;

  @BeforeEach
  void setup() {
    orderRepository.deleteAll();
    addressRepository.deleteAll();
    cartRepository.deleteAll();
    userRepository.deleteAll();

    user1 =
        userRepository.save(
            User.builder()
                .email("customer1@mail.com")
                .fullname("Customer 1")
                .password("123456")
                .role(3)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build());

    user2 =
        userRepository.save(
            User.builder()
                .email("admin1@mail.com")
                .fullname("Admin 1")
                .password("123456")
                .role(1)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build());
  }

  // GET all customers

  @Test
  void getAllCustomers_shouldReturnPaged() throws Exception {
    mockMvc
        .perform(get("/api/user/customer"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customers").isArray());
  }

  // GET all admins

  @Test
  void getAllAdmins_shouldReturnPaged() throws Exception {
    mockMvc
        .perform(get("/api/user/admin"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.admins").isArray());
  }

  // GET user by id

  @Test
  void getUserById_shouldReturnUser() throws Exception {
    mockMvc
        .perform(get("/api/user/{id}", user1.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("customer1@mail.com"));
  }

  @Test
  void getUserById_notFound() throws Exception {
    mockMvc.perform(get("/api/user/unknown-id")).andExpect(status().isNotFound());
  }

  // CREATE user

  @Test
  void createUser_success() throws Exception {
    UserDTO dto =
        UserDTO.builder()
            .email("new@mail.com")
            .fullname("New User")
            .password("123456")
            .role(3)
            .status(1)
            .build();

    mockMvc
        .perform(
            post("/api/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("new@mail.com"));
  }

  @Test
  void createUser_invalidEmail() throws Exception {
    UserDTO dto =
        UserDTO.builder().email("invalid").fullname("Bad Email").password("123456").build();

    mockMvc
        .perform(
            post("/api/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email không hợp lệ"));
  }

  @Test
  void createUser_emailExists() throws Exception {
    UserDTO dto =
        UserDTO.builder().email("customer1@mail.com").fullname("Dup").password("123456").build();

    mockMvc
        .perform(
            post("/api/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email này đã được sử dụng"));
  }

  @Test
  void createUser_passwordTooShort() throws Exception {
    UserDTO dto = UserDTO.builder().email("new@mail.com").fullname("Short").password("123").build();

    mockMvc
        .perform(
            post("/api/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Mật khẩu phải có ít nhất 6 ký tự"));
  }

  // UPDATE user

  @Test
  void updateUser_success() throws Exception {
    UserDTO dto = UserDTO.builder().email("updated@mail.com").fullname("Updated Name").build();

    mockMvc
        .perform(
            put("/api/user/{id}", user1.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("updated@mail.com"));

    User updated = userRepository.findById(user1.getId()).orElseThrow();
    assertThat(updated.getEmail()).isEqualTo("updated@mail.com");
  }

  @Test
  void updateUser_invalidEmail() throws Exception {
    UserDTO dto = UserDTO.builder().email("abc").fullname("Wrong Email").build();

    mockMvc
        .perform(
            put("/api/user/{id}", user1.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email không hợp lệ"));
  }

  @Test
  void updateUser_emailExists() throws Exception {
    UserDTO dto =
        UserDTO.builder()
            .email("admin1@mail.com") // đã tồn tại
            .build();

    mockMvc
        .perform(
            put("/api/user/{id}", user1.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Email này đã được sử dụng"));
  }

  @Test
  void updateUser_notFound() throws Exception {
    UserDTO dto = UserDTO.builder().email("a@mail.com").password("123456").build();

    mockMvc
        .perform(
            put("/api/user/unknown-id")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
  }

  // PATCH → update status

  @Test
  void updateUserStatus_success() throws Exception {
    mockMvc
        .perform(
            patch("/api/user/status/{id}", user1.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":0}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(0));

    User updated = userRepository.findById(user1.getId()).orElseThrow();
    assertThat(updated.getStatus()).isEqualTo(0);
  }

  @Test
  void updateUserStatus_missingStatus() throws Exception {
    mockMvc
        .perform(
            patch("/api/user/status/{id}", user1.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Tình trạng không để trống"));
  }

  @Test
  void updateUserStatus_notFound() throws Exception {
    mockMvc
        .perform(
            patch("/api/user/status/{id}", "unknown-id")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":1}"))
        .andExpect(status().isNotFound());
  }

  // DELETE user

  @Test
  void deleteUser_success() throws Exception {
    mockMvc
        .perform(delete("/api/user/{id}", user1.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    assertThat(userRepository.findById(user1.getId())).isNotPresent();
  }

  @Test
  void deleteUser_withOrders_shouldReturn409() throws Exception {
    orderRepository.save(
        Order.builder()
            .orderCode("ORD01")
            .fullname("Cus")
            .phone("0123")
            .speaddress("A")
            .city("C")
            .ward("W")
            .paymethod("cod")
            .status(1)
            .total(100.0)
            .user(user1)
            .build());

    mockMvc
        .perform(delete("/api/user/{id}", user1.getId()).with(csrf()))
        .andExpect(status().isConflict());
  }
}
