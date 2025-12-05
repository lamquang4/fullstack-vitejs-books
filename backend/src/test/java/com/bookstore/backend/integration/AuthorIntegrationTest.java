package com.bookstore.backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.repository.AuthorRepository;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CategoryRepository;
import com.bookstore.backend.repository.PublisherRepository;
import com.bookstore.backend.utils.SlugUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthorIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private AuthorRepository authorRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PublisherRepository publisherRepository;

  @Autowired private ObjectMapper objectMapper;

  private Author author;

  @BeforeEach
  void setup() {
    bookRepository.deleteAll();
    categoryRepository.deleteAll();
    publisherRepository.deleteAll();
    authorRepository.deleteAll();

    author =
        Author.builder()
            .fullname("Nguyen Nhat Anh")
            .slug("nguyen-nhat-anh")
            .createdAt(LocalDateTime.now())
            .build();

    authorRepository.save(author);
  }

  // -----------------------------
  // GET /api/author (paging)
  // -----------------------------
  @Test
  void testGetAllAuthorsPaging() throws Exception {
    mockMvc
        .perform(get("/api/author?page=1&limit=12"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authors[0].fullname").value("Nguyen Nhat Anh"))
        .andExpect(jsonPath("$.total").value(1));
  }

  // -----------------------------
  // GET /api/author/all
  // -----------------------------
  @Test
  void testGetAllAuthorsList() throws Exception {
    mockMvc
        .perform(get("/api/author/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].slug").value("nguyen-nhat-anh"));
  }

  // -----------------------------
  // GET /api/author/{id}
  // -----------------------------
  @Test
  void testGetAuthorById() throws Exception {
    mockMvc
        .perform(get("/api/author/{id}", author.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullname").value("Nguyen Nhat Anh"));
  }

  // -----------------------------
  // POST /api/author (success)
  // -----------------------------
  @Test
  void testCreateAuthorSuccess() throws Exception {
    Author newAuthor =
        Author.builder()
            .fullname("J. K. Rowling")
            .slug("unused") // ignored
            .build();

    try (MockedStatic<SlugUtil> mocked = org.mockito.Mockito.mockStatic(SlugUtil.class)) {
      mocked.when(() -> SlugUtil.toSlug("J. K. Rowling")).thenReturn("jk-rowling");

      mockMvc
          .perform(
              post("/api/author")
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(newAuthor)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.slug").value("jk-rowling"));
    }

    assertEquals(2, authorRepository.count());
  }

  // -----------------------------
  // POST /api/author (duplicate fullname)
  // -----------------------------
  @Test
  void testCreateAuthorDuplicate() throws Exception {
    Author duplicate = Author.builder().fullname("Nguyen Nhat Anh").slug("will-fail").build();

    mockMvc
        .perform(
            post("/api/author")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
        .andExpect(status().isBadRequest());
  }

  // -----------------------------
  // PUT /api/author/{id} (success)
  // -----------------------------
  @Test
  void testUpdateAuthorSuccess() throws Exception {
    Author updateData = Author.builder().fullname("Updated Name").slug("old").build();

    try (MockedStatic<SlugUtil> mocked = org.mockito.Mockito.mockStatic(SlugUtil.class)) {
      mocked.when(() -> SlugUtil.toSlug("Updated Name")).thenReturn("updated-name");

      mockMvc
          .perform(
              put("/api/author/{id}", author.getId())
                  .with(csrf())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateData)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.fullname").value("Updated Name"))
          .andExpect(jsonPath("$.slug").value("updated-name"));
    }
  }

  // -----------------------------
  // PUT /api/author/{id} (duplicate fullname with another author)
  // -----------------------------
  @Test
  void testUpdateAuthorDuplicateName() throws Exception {
    Author other = Author.builder().fullname("Existing Name").slug("existing-name").build();
    authorRepository.save(other);

    Author updateData = Author.builder().fullname("Existing Name").build();

    mockMvc
        .perform(
            put("/api/author/{id}", author.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
        .andExpect(status().isBadRequest());
  }

  // -----------------------------
  // DELETE /api/author/{id} (author HAS books → 400)
  // -----------------------------
  @Test
  void testDeleteAuthorHasBooks() throws Exception {

    Category category = Category.builder().name("Novel").slug("novel").status(1).build();
    categoryRepository.save(category);

    Publisher publisher = Publisher.builder().name("NXB Tre").slug("nxb-tre").build();
    publisherRepository.save(publisher);

    Book book =
        Book.builder()
            .title("Test Book")
            .price(100.0)
            .discount(0.0)
            .description("desc")
            .publicationDate(LocalDate.of(2024, 1, 1))
            .numberOfPages(100)
            .weight(0.3)
            .width(10.0)
            .length(20.0)
            .thickness(1.0)
            .slug("test-book")
            .status(1)
            .stock(10)
            .category(category)
            .publisher(publisher)
            .author(author)
            .build();

    bookRepository.save(book);

    mockMvc
        .perform(delete("/api/author/{id}", author.getId()).with(csrf()))
        .andExpect(status().isConflict());
  }

  // -----------------------------
  // DELETE /api/author/{id} (success)
  // -----------------------------
  @Test
  void testDeleteAuthorSuccess() throws Exception {
    mockMvc
        .perform(delete("/api/author/{id}", author.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    assertEquals(0, authorRepository.count());
  }
}
