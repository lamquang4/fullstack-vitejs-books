package com.bookstore.backend.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private AuthorService authorService;

  @Autowired private ObjectMapper objectMapper;

  private Author author;

  private final String AUTHOR_ID = "author123";

  @BeforeEach
  void setup() {

    author =
        Author.builder()
            .id(AUTHOR_ID)
            .fullname("Nguyễn Nhật Ánh")
            .slug("nguyen-nhat-anh")
            .createdAt(LocalDateTime.now())
            .build();
  }

  // Lấy tất cả tác giả có phân trang
  @Test
  @WithMockUser
  void getAllAuthors_shouldReturnPagingResult() throws Exception {

    Page<Author> page = new PageImpl<>(List.of(author), PageRequest.of(0, 12), 1);

    Mockito.when(authorService.getAllAuthors(1, 12, null)).thenReturn(page);

    mockMvc
        .perform(get("/api/author"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authors[0].id").value(AUTHOR_ID))
        .andExpect(jsonPath("$.authors[0].fullname").value("Nguyễn Nhật Ánh"))
        .andExpect(jsonPath("$.authors[0].slug").value("nguyen-nhat-anh"))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.total").value(1));
  }

  // Lấy tất cả tác giả không phân trang
  @Test
  @WithMockUser
  void getAllAuthors1_shouldReturnList() throws Exception {

    Mockito.when(authorService.getAllAuthors1()).thenReturn(List.of(author));

    mockMvc
        .perform(get("/api/author/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(AUTHOR_ID))
        .andExpect(jsonPath("$[0].fullname").value("Nguyễn Nhật Ánh"));
  }

  // Láy tác giả theo id
  @Test
  @WithMockUser
  void getAuthorById_shouldReturnAuthor() throws Exception {

    Mockito.when(authorService.getAuthorById(AUTHOR_ID)).thenReturn(author);

    mockMvc
        .perform(get("/api/author/{id}", AUTHOR_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullname").value("Nguyễn Nhật Ánh"))
        .andExpect(jsonPath("$.slug").value("nguyen-nhat-anh"));
  }

  // Thêm tác giả
  @Test
  @WithMockUser
  void createAuthor_shouldReturnCreatedAuthor() throws Exception {

    Mockito.when(authorService.createAuthor(Mockito.any(Author.class))).thenReturn(author);

    mockMvc
        .perform(
            post("/api/author")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(author)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullname").value("Nguyễn Nhật Ánh"));
  }

  // Cập nhật tác giả
  @Test
  @WithMockUser
  void updateAuthor_shouldReturnUpdatedAuthor() throws Exception {

    Mockito.when(authorService.updateAuthor(Mockito.eq(AUTHOR_ID), Mockito.any(Author.class)))
        .thenReturn(author);

    mockMvc
        .perform(
            put("/api/author/{id}", AUTHOR_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(author)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(AUTHOR_ID))
        .andExpect(jsonPath("$.slug").value("nguyen-nhat-anh"));
  }

  // Xóa tác giả-
  @Test
  @WithMockUser
  void deleteAuthor_shouldReturnNoContent() throws Exception {

    Mockito.doNothing().when(authorService).deleteAuthor(AUTHOR_ID);

    mockMvc
        .perform(delete("/api/author/{id}", AUTHOR_ID).with(csrf()))
        .andExpect(status().isNoContent());
  }
}
