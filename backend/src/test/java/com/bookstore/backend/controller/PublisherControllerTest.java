package com.bookstore.backend.controller;

import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.service.PublisherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublisherController.class)
class PublisherControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private PublisherService publisherService;

        private final ObjectMapper mapper = JsonMapper.builder()
                        .addModule(new JavaTimeModule())
                        .build();

        private Publisher mockPublisher() {
                return Publisher.builder()
                                .id("pub1")
                                .name("Test Publisher")
                                .slug("test-publisher")
                                .createdAt(LocalDateTime.now())
                                .build();
        }

        // Lấy tất cả nhà xuất bản có phân trang
        @Test
        @WithMockUser
        void getAllPublishers_shouldReturnPage() throws Exception {

                List<Publisher> publishers = List.of(mockPublisher());
                Page<Publisher> page = new PageImpl<>(publishers);

                Mockito.when(publisherService.getAllPublishers(
                                anyInt(), anyInt(), anyString()))
                                .thenReturn(page);

                mockMvc.perform(get("/api/publisher")
                                .param("page", "1")
                                .param("limit", "12")
                                .param("q", "test"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.publishers[0].id").value("pub1"))
                                .andExpect(jsonPath("$.publishers[0].name")
                                                .value("Test Publisher"))
                                .andExpect(jsonPath("$.totalPages").value(1))
                                .andExpect(jsonPath("$.total").value(1));
        }

        // Láy tất cả nhà xuất bản không phân trang
        @Test
        @WithMockUser
        void getAllPublishers1_shouldReturnList() throws Exception {

                Mockito.when(publisherService.getAllPublishers1())
                                .thenReturn(List.of(mockPublisher()));

                mockMvc.perform(get("/api/publisher/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value("pub1"))
                                .andExpect(jsonPath("$[0].name").value("Test Publisher"));
        }

        // Lấy nhà xuất bản theo id - tìm thấy
        @Test
        @WithMockUser
        void getPublisherById_found() throws Exception {

                Mockito.when(publisherService.getPublisherById("pub1"))
                                .thenReturn(Optional.of(mockPublisher()));

                mockMvc.perform(get("/api/publisher/{id}", "pub1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("pub1"))
                                .andExpect(jsonPath("$.name").value("Test Publisher"));
        }

        // Lấy nhà xuất bản theo id - không tìm thấy
        @Test
        @WithMockUser
        void getPublisherById_notFound() throws Exception {

                Mockito.when(publisherService.getPublisherById("404"))
                                .thenReturn(Optional.empty());

                mockMvc.perform(get("/api/publisher/{id}", "404"))
                                .andExpect(status().isNotFound());
        }

        // Thêm nhà xuất bản
        @Test
        @WithMockUser
        void createPublisher_shouldReturnPublisher() throws Exception {

                Publisher publisher = mockPublisher();

                Mockito.when(publisherService.createPublisher(any()))
                                .thenReturn(publisher);

                mockMvc.perform(post("/api/publisher")
                                .with(csrf())
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(publisher)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("pub1"))
                                .andExpect(jsonPath("$.name")
                                                .value("Test Publisher"));
        }

        // Cập nhật nhà xuất bản
        @Test
        @WithMockUser
        void updatePublisher_shouldUpdate() throws Exception {

                Publisher updated = mockPublisher();
                updated.setName("Updated Publisher");

                Mockito.when(publisherService.updatePublisher(eq("pub1"), any()))
                                .thenReturn(updated);

                mockMvc.perform(put("/api/publisher/{id}", "pub1")
                                .with(csrf())
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(updated)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name")
                                                .value("Updated Publisher"));
        }

        // Cập nhật nhà xuất bản - không tìm thấy
        @Test
        @WithMockUser
        void updatePublisher_notFound() throws Exception {

                Mockito.when(publisherService.updatePublisher(eq("404"), any()))
                                .thenReturn(null);

                mockMvc.perform(put("/api/publisher/{id}", "404")
                                .with(csrf())
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(mockPublisher())))
                                .andExpect(status().isNotFound());
        }

        // Xóa nhà xuất bản
        @Test
        @WithMockUser
        void deletePublisher_shouldReturn204() throws Exception {

                Mockito.doNothing().when(publisherService)
                                .deletePublisher("pub1");

                mockMvc.perform(delete("/api/publisher/{id}", "pub1")
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

}
