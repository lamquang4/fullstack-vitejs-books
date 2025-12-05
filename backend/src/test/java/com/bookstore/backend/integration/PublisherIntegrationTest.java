package com.bookstore.backend.integration;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.repository.AuthorRepository;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CategoryRepository;
import com.bookstore.backend.repository.PublisherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser
class PublisherIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    @Autowired private PublisherRepository publisherRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Publisher pub1;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        publisherRepository.deleteAll();

        pub1 = publisherRepository.save(
                Publisher.builder()
                        .name("NXB Kim Đồng")
                        .slug("nxb-kim-dong")
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    // --------------------- GET paged ---------------------
    @Test
    void getAllPublishers_shouldReturnPaged() throws Exception {

        publisherRepository.save(
                Publisher.builder()
                        .name("NXB Trẻ")
                        .slug("nxb-tre")
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        mockMvc.perform(get("/api/publisher")
                        .param("page", "1")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.publishers").isArray())
                .andExpect(jsonPath("$.total").value(2));
    }

    // --------------------- GET all (unpaged) ---------------------
    @Test
    void getAllPublishers1_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/publisher/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // --------------------- GET by id ---------------------
    @Test
    void getPublisherById_shouldReturnPublisher() throws Exception {
        mockMvc.perform(get("/api/publisher/{id}", pub1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NXB Kim Đồng"));
    }

    @Test
    void getPublisherById_notFound() throws Exception {
        mockMvc.perform(get("/api/publisher/{id}", "unknown"))
                .andExpect(status().isNotFound());
    }

    // --------------------- CREATE publisher ---------------------
    @Test
    void createPublisher_shouldCreateSuccessfully() throws Exception {

        Publisher payload =
                Publisher.builder()
                        .name("Nhà Xuất Bản Giáo Dục")
                        .build();

        mockMvc.perform(post("/api/publisher")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("nha-xuat-ban-giao-duc"));

        assertThat(publisherRepository.findByName("Nhà Xuất Bản Giáo Dục")).isPresent();
    }

    @Test
    void createPublisher_duplicateName_shouldReturn400() throws Exception {

        Publisher payload = Publisher.builder().name("NXB Kim Đồng").build();

        mockMvc.perform(post("/api/publisher")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    // --------------------- UPDATE publisher ---------------------
    @Test
    void updatePublisher_shouldUpdateSuccessfully() throws Exception {

        Publisher payload = Publisher.builder().name("NXB Đồng Tâm").build();

        mockMvc.perform(put("/api/publisher/{id}", pub1.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("nxb-dong-tam"));

        Publisher updated = publisherRepository.findById(pub1.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("NXB Đồng Tâm");
    }

    @Test
    void updatePublisher_duplicateName_shouldReturn400() throws Exception {

        Publisher pub2 = publisherRepository.save(
                Publisher.builder()
                        .name("NXB Trẻ")
                        .slug("nxb-tre")
                        .build()
        );

        Publisher payload = Publisher.builder().name("NXB Trẻ").build();

        mockMvc.perform(put("/api/publisher/{id}", pub1.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePublisher_notFound_shouldReturn404() throws Exception {

        Publisher payload = Publisher.builder().name("ABC").build();

        mockMvc.perform(put("/api/publisher/{id}", "unknown")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());
    }

    // --------------------- DELETE publisher ---------------------
    @Test
    void deletePublisher_shouldDeleteSuccessfully() throws Exception {

        mockMvc.perform(delete("/api/publisher/{id}", pub1.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(publisherRepository.findById(pub1.getId())).isNotPresent();
    }

    @Test
    void deletePublisher_withLinkedBooks_shouldReturn409() throws Exception {

        // tạo author
        Author author = authorRepository.save(
                Author.builder()
                        .fullname("Author A")
                        .slug("author-a")
                        .build()
        );

        // tạo category
        Category category = categoryRepository.save(
                Category.builder()
                        .name("Category A")
                        .slug("category-a")
                        .status(1)
                        .build()
        );

        // tạo book có đủ quan hệ bắt buộc
        Book book = bookRepository.save(
                Book.builder()
                        .title("Book A")
                        .slug("book-a")
                        .description("test book")
                        .price(100.0)
                        .discount(0.0)
                        .numberOfPages(100)
                        .width(10.0)
                        .length(20.0)
                        .thickness(1.0)
                        .weight(0.5)
                        .status(1)
                        .publicationDate(java.time.LocalDate.of(2021, 1, 1))
                        .stock(10)
                        .publisher(pub1)     // liên kết publisher cần xóa
                        .author(author)       // BẮT BUỘC
                        .category(category)   // BẮT BUỘC
                        .build()
        );

        mockMvc.perform(delete("/api/publisher/{id}", pub1.getId())
                        .with(csrf()))
                .andExpect(status().isConflict()); // 409
    }

}
