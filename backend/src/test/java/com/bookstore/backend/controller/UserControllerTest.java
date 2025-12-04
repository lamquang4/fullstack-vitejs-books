package com.bookstore.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.dto.UserDTO;
import com.bookstore.backend.entities.User;
import com.bookstore.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  private final ObjectMapper mapper = new ObjectMapper();

  private UserDTO mockUserDTO() {
    return UserDTO.builder()
        .id("u1")
        .email("user@test.com")
        .fullname("Test User")
        .password("123456")
        .role(3)
        .status(1)
        .build();
  }

  private User mockUserEntity() {
    return User.builder()
        .id("u1")
        .email("user@test.com")
        .fullname("Test User")
        .password("123456")
        .role(3)
        .status(1)
        .build();
  }

  // Lấy tất cả khách hàng
  @Test
  @WithMockUser
  void getAllCustomers_shouldReturnPage() throws Exception {

    Page<UserDTO> page = new PageImpl<>(List.of(mockUserDTO()));

    Mockito.when(userService.getAllCustomers(anyInt(), anyInt(), any(), any())).thenReturn(page);

    mockMvc
        .perform(get("/api/user/customer").param("page", "1").param("limit", "12"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customers[0].id").value("u1"))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.total").value(1));
  }

  // Lấy tất cả quản trị viên
  @Test
  @WithMockUser
  void getAllAdmins_shouldReturnPage() throws Exception {

    Page<UserDTO> page = new PageImpl<>(List.of(mockUserDTO()));

    Mockito.when(userService.getAllAdmins(anyInt(), anyInt(), any(), any())).thenReturn(page);

    mockMvc
        .perform(get("/api/user/admin").param("page", "1").param("limit", "12"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.admins[0].id").value("u1"))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.total").value(1));
  }

  // lấy người dùng theo id
  @Test
  @WithMockUser
  void getUserById_success() throws Exception {

    Mockito.when(userService.getUserById("u1")).thenReturn(Optional.of(mockUserDTO()));

    mockMvc
        .perform(get("/api/user/{id}", "u1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("u1"))
        .andExpect(jsonPath("$.email").value("user@test.com"));
  }

  @Test
  @WithMockUser
  void getUserById_notFound() throws Exception {

    Mockito.when(userService.getUserById("404")).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/user/{id}", "404")).andExpect(status().isNotFound());
  }

  // thêm người dùng
  @Test
  @WithMockUser
  void createUser_shouldReturnCreatedUser() throws Exception {

    Mockito.when(userService.createUser(any())).thenReturn(mockUserDTO());

    mockMvc
        .perform(
            post("/api/user")
                .with(csrf())
                .contentType("application/json")
                .content(mapper.writeValueAsString(mockUserDTO())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("u1"))
        .andExpect(jsonPath("$.fullname").value("Test User"));
  }

  // cập nhật người dùng
  @Test
  @WithMockUser
  void updateUser_success() throws Exception {

    UserDTO updated = mockUserDTO();
    updated.setFullname("Updated Name");

    Mockito.when(userService.updateUser(eq("u1"), any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/user/{id}", "u1")
                .with(csrf())
                .contentType("application/json")
                .content(mapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullname").value("Updated Name"));
  }

  @Test
  @WithMockUser
  void updateUser_notFound() throws Exception {

    Mockito.when(userService.updateUser(eq("404"), any())).thenReturn(null);

    mockMvc
        .perform(
            put("/api/user/{id}", "404")
                .with(csrf())
                .contentType("application/json")
                .content(mapper.writeValueAsString(mockUserDTO())))
        .andExpect(status().isNotFound());
  }

  // cập nhật status người dùng
  @Test
  @WithMockUser
  void updateUserStatus_shouldReturnUpdatedStatus() throws Exception {

    Mockito.when(userService.updateUserStatus(eq("u1"), eq(0))).thenReturn(mockUserEntity());

    mockMvc
        .perform(
            patch("/api/user/status/{id}", "u1")
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":0}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("u1"))
        .andExpect(jsonPath("$.status").value(1));
  }

  @Test
  @WithMockUser
  void updateUserStatus_missingStatus_shouldThrow400() throws Exception {

    mockMvc
        .perform(
            patch("/api/user/status/{id}", "u1")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
        .andExpect(status().isBadRequest());
  }

  // xóa người dùng
  @Test
  @WithMockUser
  void deleteUser_shouldReturn204() throws Exception {

    Mockito.doNothing().when(userService).deleteUser("u1");

    mockMvc.perform(delete("/api/user/{id}", "u1").with(csrf())).andExpect(status().isNoContent());
  }
}
