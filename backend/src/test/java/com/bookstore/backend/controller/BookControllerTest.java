package com.bookstore.backend.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.dto.AuthorDTO;
import com.bookstore.backend.dto.BookDTO;
import com.bookstore.backend.dto.BookDetailDTO;
import com.bookstore.backend.dto.CategoryDTO;
import com.bookstore.backend.dto.ImageBookDTO;
import com.bookstore.backend.dto.PublisherDTO;
import com.bookstore.backend.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private BookService bookService;

  private BookDTO bookDTO;
  private BookDetailDTO bookDetailDTO;
  private ObjectMapper mapper;

  @BeforeEach
  void setup() {

    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());

    AuthorDTO author = new AuthorDTO("a1", "Nguyễn Nhật Ánh", "nguyen-nhat-anh");
    PublisherDTO publisher = new PublisherDTO("p1", "NXB Trẻ", "nxb-tre");
    CategoryDTO category = new CategoryDTO("c1", "Thiếu nhi", "thieu-nhi");

    ImageBookDTO image = new ImageBookDTO("img1", "https://test/image.jpg", "2025-11-29T00:00:00");

    bookDTO =
        new BookDTO(
            "b1",
            "Kính vạn hoa",
            50000,
            10,
            "kinh-van-hoa",
            100,
            1,
            "2025-11-29T00:00:00",
            author,
            publisher,
            category,
            150,
            List.of(image));

    bookDetailDTO =
        new BookDetailDTO(
            "b1",
            "Kính vạn hoa",
            50000d,
            10d,
            "Sách truyện thiếu nhi",
            "kinh-van-hoa",
            150,
            "2024-01-01",
            300d,
            20d,
            15d,
            2d,
            100,
            1,
            "2025-11-29T00:00:00",
            author,
            publisher,
            category,
            List.of(image));
  }

  // Lấy sách
  @Test
  @WithMockUser
  void getAllBooks_shouldReturnBooks() throws Exception {

    Page<BookDTO> page = new PageImpl<>(List.of(bookDTO));

    Mockito.when(bookService.getAllBooks(1, 12, null, null)).thenReturn(page);

    mockMvc
        .perform(get("/api/book").param("page", "1").param("limit", "12"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.books[0].title").value("Kính vạn hoa"));
  }

  @Test
  @WithMockUser
  void getBySlug_shouldReturnDetail() throws Exception {

    Mockito.when(bookService.getBookBySlug("kinh-van-hoa")).thenReturn(bookDetailDTO);

    mockMvc
        .perform(get("/api/book/slug/{slug}", "kinh-van-hoa"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Sách truyện thiếu nhi"));
  }

  @Test
  @WithMockUser
  void getBestsellerBooks() throws Exception {

    Mockito.when(bookService.getAllBooksByTotalSold()).thenReturn(List.of(bookDTO));

    mockMvc.perform(get("/api/book/bestseller")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void getBookById_shouldReturnDetail() throws Exception {

    Mockito.when(bookService.getBookById("b1")).thenReturn(bookDetailDTO);

    mockMvc
        .perform(get("/api/book/{id}", "b1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.slug").value("kinh-van-hoa"));
  }

  // Cập nhật sách
  @Test
  @WithMockUser
  void updateStatus_shouldReturnUpdatedStatus() throws Exception {

    Mockito.when(bookService.updateBookStatus("b1", 0))
        .thenReturn(com.bookstore.backend.entities.Book.builder().id("b1").status(0).build());

    mockMvc
        .perform(
            patch("/api/book/status/{id}", "b1")
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":0}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(0));
  }

  // Xóa sách
  @Test
  @WithMockUser
  void deleteBook_shouldReturnNoContent() throws Exception {

    Mockito.doNothing().when(bookService).deleteBook("b1");

    mockMvc.perform(delete("/api/book/{id}", "b1").with(csrf())).andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser
  void deleteImage_shouldReturnNoContent() throws Exception {

    Mockito.doNothing().when(bookService).deleteImageBook("img1");

    mockMvc
        .perform(delete("/api/book/image/{id}", "img1").with(csrf()))
        .andExpect(status().isNoContent());
  }

  // Multipart
  @Test
  @WithMockUser
  void createBook_withMultipart_shouldReturnOk() throws Exception {

    String json = mapper.writeValueAsString(bookDetailDTO);

    MockMultipartFile book = new MockMultipartFile("book", "", "application/json", json.getBytes());

    MockMultipartFile image =
        new MockMultipartFile("files", "test.jpg", "image/jpeg", "fake".getBytes());

    Mockito.when(bookService.createBook(Mockito.any(), Mockito.any()))
        .thenReturn(com.bookstore.backend.entities.Book.builder().id("b1").build());

    mockMvc
        .perform(multipart("/api/book").file(book).file(image).with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void updateBook_shouldReturnUpdated() throws Exception {

    String json = mapper.writeValueAsString(bookDetailDTO);

    MockMultipartFile book = new MockMultipartFile("book", "", "application/json", json.getBytes());

    Mockito.when(bookService.updateBook(Mockito.eq("b1"), Mockito.any(), Mockito.any()))
        .thenReturn(com.bookstore.backend.entities.Book.builder().id("b1").build());

    mockMvc
        .perform(
            multipart("/api/book/{id}", "b1")
                .file(book)
                .with(csrf())
                .with(
                    r -> {
                      r.setMethod("PUT");
                      return r;
                    }))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void updateImages_shouldReturnNoContent() throws Exception {

    MockMultipartFile image =
        new MockMultipartFile("files", "new.jpg", "image/jpeg", "123".getBytes());

    Mockito.doNothing().when(bookService).updateImagesBook(Mockito.any(), Mockito.any());

    mockMvc
        .perform(
            multipart("/api/book/image")
                .file(image)
                .with(csrf())
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    }))
        .andExpect(status().isNoContent());
  }
}
