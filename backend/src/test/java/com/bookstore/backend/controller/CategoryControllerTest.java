package com.bookstore.backend.controller;

import com.bookstore.backend.entities.Category;
import com.bookstore.backend.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CategoryService categoryService;

        @Autowired
        private ObjectMapper objectMapper;

        private Category category;

        @BeforeEach
        void setup() {
                category = Category.builder()
                                .id("c1")
                                .name("Thiếu nhi")
                                .slug("thieu-nhi")
                                .status(1)
                                // KHÔNG set createdAt → tránh Jackson lỗi
                                .build();
        }

        // Lấy tất cả danh mục có phân trang
        @Test
        @WithMockUser
        void getAllCategories() throws Exception {

                Page<Category> page = new PageImpl<>(List.of(category));

                Mockito.when(categoryService.getAllCategories(
                                Mockito.eq(1),
                                Mockito.eq(12),
                                Mockito.any(),
                                Mockito.any()))
                                .thenReturn(page);

                mockMvc.perform(
                                get("/api/category")
                                                .param("page", "1")
                                                .param("limit", "12"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.categories", hasSize(1)))
                                .andExpect(jsonPath("$.categories[0].name")
                                                .value("Thiếu nhi"));
        }

        // Lấy tất cả danh mục không phân trang
        @Test
        @WithMockUser
        void getAllCategories1() throws Exception {

                Mockito.when(categoryService.getAllCategories1())
                                .thenReturn(List.of(category));

                mockMvc.perform(get("/api/category/all"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].slug")
                                                .value("thieu-nhi"));
        }

        // Láy danh mục theo id
        @Test
        @WithMockUser
        void getById_found() throws Exception {

                Mockito.when(categoryService.getCategoryById("c1"))
                                .thenReturn(Optional.of(category));

                mockMvc.perform(get("/api/category/{id}", "c1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id")
                                                .value("c1"));
        }

        @Test
        @WithMockUser
        void getById_notFound() throws Exception {

                Mockito.when(categoryService.getCategoryById("xxx"))
                                .thenReturn(Optional.empty());

                mockMvc.perform(get("/api/category/{id}", "xxx"))
                                .andExpect(status().isNotFound());
        }

        // Lấy danh mục có status = 1
        @Test
        @WithMockUser
        void getActiveCategories() throws Exception {

                Mockito.when(categoryService.getActiveCategoriesWithActiveBooks())
                                .thenReturn(List.of(category));

                mockMvc.perform(get("/api/category/active"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.categories[0].name")
                                                .value("Thiếu nhi"));
        }

        // Thêm danh mục
        @Test
        @WithMockUser
        void createCategory() throws Exception {

                Mockito.when(categoryService.createCategory(
                                Mockito.any(Category.class)))
                                .thenReturn(category);

                mockMvc.perform(
                                post("/api/category")
                                                .with(csrf())
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(category)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.slug")
                                                .value("thieu-nhi"));
        }

        // Cập nhật danh mục
        @Test
        @WithMockUser
        void updateCategory_found() throws Exception {

                Mockito.when(categoryService.updateCategory(
                                Mockito.eq("c1"),
                                Mockito.any(Category.class)))
                                .thenReturn(category);

                mockMvc.perform(
                                put("/api/category/{id}", "c1")
                                                .with(csrf())
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(category)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name")
                                                .value("Thiếu nhi"));
        }

        @Test
        @WithMockUser
        void updateCategory_notFound() throws Exception {

                Mockito.when(categoryService.updateCategory(
                                Mockito.eq("c1"),
                                Mockito.any(Category.class)))
                                .thenReturn(null);

                mockMvc.perform(
                                put("/api/category/{id}", "c1")
                                                .with(csrf())
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(category)))
                                .andExpect(status().isNotFound());
        }

        // Cập nhật status danh mục
        @Test
        @WithMockUser
        void updateCategoryStatus() throws Exception {

                category.setStatus(0);

                Mockito.when(categoryService.updateCategoryStatus(
                                Mockito.eq("c1"),
                                Mockito.eq(0)))
                                .thenReturn(category);

                mockMvc.perform(
                                patch("/api/category/status/{id}", "c1")
                                                .with(csrf())
                                                .contentType("application/json")
                                                .content(objectMapper.writeValueAsString(
                                                                Map.of("status", 0))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status")
                                                .value(0));
        }

        // Xóa danh mục
        @Test
        @WithMockUser
        void deleteCategory() throws Exception {

                Mockito.doNothing()
                                .when(categoryService)
                                .deleteCategory("c1");

                mockMvc.perform(
                                delete("/api/category/{id}", "c1")
                                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }
}
