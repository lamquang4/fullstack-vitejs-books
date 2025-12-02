package com.bookstore.backend.controller;

import com.bookstore.backend.dto.LoginRequest;
import com.bookstore.backend.dto.LoginResponse;
import com.bookstore.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @Autowired
        private ObjectMapper objectMapper;

        private LoginRequest loginRequest;
        private LoginResponse loginResponse;
        private Map<String, Object> mockUser;

        @BeforeEach
        void setup() {

                loginRequest = new LoginRequest("user@gmail.com", "123456");

                loginResponse = LoginResponse.builder()
                                .token("mock-jwt-token")
                                .id("user123")
                                .email("user@gmail.com")
                                .fullname("Nguyen Van A")
                                .role(3)
                                .build();

                mockUser = Map.of(
                                "id", "user123",
                                "email", "user@gmail.com",
                                "fullname", "Nguyen Van A",
                                "role", 3);
        }

        // Đăng nhập
        @Test
        @WithMockUser
        void login_shouldReturnToken() throws Exception {

                Mockito.when(authService.login(Mockito.any(LoginRequest.class)))
                                .thenReturn(loginResponse);

                mockMvc.perform(
                                post("/api/auth/login")
                                                .with(csrf())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                                .andExpect(jsonPath("$.id").value("user123"))
                                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                                .andExpect(jsonPath("$.role").value(3));
        }

        // Lấy thông tin tài khoản không token
        @Test
        void getMe_withoutToken_shouldReturn401() throws Exception {

                mockMvc.perform(get("/api/auth/me"))
                                .andExpect(status().isUnauthorized());
        }

        // Lấy thông tin tài khoản có token
        @Test
        @WithMockUser
        void getMe_withValidToken_shouldReturnUser() throws Exception {

                String token = "mock-jwt-token";

                Mockito.when(authService.getUserFromToken(token))
                                .thenReturn(mockUser);

                mockMvc.perform(get("/api/auth/me")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                                .andExpect(jsonPath("$.role").value(3));
        }
}
