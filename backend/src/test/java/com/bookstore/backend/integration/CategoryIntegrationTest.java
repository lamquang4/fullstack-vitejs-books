package com.bookstore.backend.integration;

import static org.hamcrest.Matchers.containsInAnyOrder;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
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
class CategoryIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper mapper;

  @Autowired private CategoryRepository categoryRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private PublisherRepository publisherRepository;

  private Category cat1;
  private Category cat2;
  private Category cat3;

  @BeforeEach
  void setup() {
    bookRepository.deleteAll();
    categoryRepository.deleteAll();
    authorRepository.deleteAll();
    publisherRepository.deleteAll();

    cat1 =
        categoryRepository.save(
            Category.builder()
                .name("Fiction")
                .slug("fiction")
                .status(1)
                .createdAt(LocalDateTime.now())
                .build());

    cat2 =
        categoryRepository.save(
            Category.builder()
                .name("History")
                .slug("history")
                .status(0)
                .createdAt(LocalDateTime.now())
                .build());

    cat3 =
        categoryRepository.save(
            Category.builder()
                .name("Science")
                .slug("science")
                .status(1)
                .createdAt(LocalDateTime.now())
                .build());
  }

  // ------------------------------------------------------------
  @Test
  void getAllCategories_shouldReturnPaged() throws Exception {
    mockMvc
        .perform(get("/api/category").param("page", "1").param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categories").isArray())
        .andExpect(jsonPath("$.total").value(3));
  }

  // ------------------------------------------------------------
  @Test
  void getAllCategories1_shouldReturnAll() throws Exception {
    mockMvc
        .perform(get("/api/category/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[*].name", containsInAnyOrder("Fiction", "Science", "History")));
  }

  // ------------------------------------------------------------
  @Test
  void getCategoryById_found() throws Exception {
    mockMvc
        .perform(get("/api/category/" + cat1.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Fiction"));
  }

  @Test
  void getCategoryById_notFound() throws Exception {
    mockMvc.perform(get("/api/category/xxx")).andExpect(status().isNotFound());
  }

  // ------------------------------------------------------------
  @Test
  void getActiveCategoriesWithActiveBooks_shouldReturnOnlyActiveOnes() throws Exception {

    // tạo author + publisher + 1 book active trong cat1
    Author author =
        authorRepository.save(Author.builder().fullname("John Writer").slug("john-writer").build());

    Publisher pub =
        publisherRepository.save(Publisher.builder().name("Pub House").slug("pub-house").build());

    bookRepository.save(
        Book.builder()
            .title("Book 1")
            .slug("book-1")
            .price(100.0)
            .discount(10.0)
            .description("desc")
            .publicationDate(LocalDate.of(2020, 1, 1))
            .numberOfPages(100)
            .weight(1.0)
            .width(1.0)
            .length(1.0)
            .thickness(1.0)
            .status(1)
            .stock(10)
            .category(cat1)
            .author(author)
            .publisher(pub)
            .createdAt(LocalDateTime.now())
            .build());

    mockMvc
        .perform(get("/api/category/active"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.categories[0].name").value("Fiction"))
        .andExpect(jsonPath("$.categories.length()").value(1));
  }

  // ------------------------------------------------------------
  @Test
  void createCategory_success() throws Exception {
    Category newCat = Category.builder().name("Technology").status(1).build();

    mockMvc
        .perform(
            post("/api/category")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newCat)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.slug").value("technology"));
  }

  @Test
  void createCategory_duplicateName_shouldFail400() throws Exception {
    Category dup = Category.builder().name("Fiction").status(1).build();

    mockMvc
        .perform(
            post("/api/category")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dup)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Tên danh mục đã tồn tại"));
  }

  // ------------------------------------------------------------
  @Test
  void updateCategory_success() throws Exception {

    Category updateData = Category.builder().name("New Fiction").status(1).build();

    mockMvc
        .perform(
            put("/api/category/" + cat1.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updateData)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Fiction"));
  }

  @Test
  void updateCategory_notFound() throws Exception {
    Category c = Category.builder().name("X").status(1).build();

    mockMvc
        .perform(
            put("/api/category/xxx")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(c)))
        .andExpect(status().isNotFound());
  }

  // ------------------------------------------------------------
  @Test
  void updateCategoryStatus_shouldHideBooks() throws Exception {

    // tạo author + publisher + active book trong cat1
    Author author = authorRepository.save(Author.builder().fullname("A").slug("a").build());
    Publisher pub = publisherRepository.save(Publisher.builder().name("P").slug("p").build());

    Book book =
        bookRepository.save(
            Book.builder()
                .title("Book Hide")
                .slug("book-hide")
                .price(100.0)
                .discount(10.0)
                .description("desc")
                .publicationDate(LocalDate.now())
                .numberOfPages(100)
                .weight(1.0)
                .width(1.0)
                .length(1.0)
                .thickness(1.0)
                .status(1)
                .stock(10)
                .category(cat1)
                .author(author)
                .publisher(pub)
                .build());

    mockMvc
        .perform(
            patch("/api/category/status/" + cat1.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("status", 0))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(0));

    // verify book is hidden
    Book updated = bookRepository.findById(book.getId()).orElseThrow();
    assert updated.getStatus() == 0;
  }

  // ------------------------------------------------------------
  @Test
  void deleteCategory_success() throws Exception {

    mockMvc
        .perform(delete("/api/category/" + cat2.getId()).with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteCategory_hasBooks_shouldReturn409() throws Exception {

    Author author = authorRepository.save(Author.builder().fullname("A").slug("a").build());
    Publisher pub = publisherRepository.save(Publisher.builder().name("P").slug("p").build());

    bookRepository.save(
        Book.builder()
            .title("Book 1")
            .slug("book-1")
            .price(100.0)
            .discount(10.0)
            .description("desc")
            .publicationDate(LocalDate.now())
            .numberOfPages(100)
            .weight(1.0)
            .width(1.0)
            .length(1.0)
            .thickness(1.0)
            .status(1)
            .stock(10)
            .category(cat1)
            .author(author)
            .publisher(pub)
            .build());

    mockMvc
        .perform(delete("/api/category/" + cat1.getId()).with(csrf()))
        .andExpect(status().isConflict());
  }

  @Test
  void deleteCategory_notFound() throws Exception {
    mockMvc.perform(delete("/api/category/xxx").with(csrf())).andExpect(status().isNotFound());
  }
}
