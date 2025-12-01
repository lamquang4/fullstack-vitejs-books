package com.bookstore.backend.service;

import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookService_UpdateStatusTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book bookActiveCategory;
    private Book bookHiddenCategory;

    @BeforeEach
    void setup() {

        Category activeCategory = Category.builder()
                .id("cat1")
                .status(1)
                .name("Education")
                .build();

        Category hiddenCategory = Category.builder()
                .id("cat2")
                .status(0)
                .name("Hidden Cat")
                .build();

        bookActiveCategory = Book.builder()
                .id("book1")
                .status(0)
                .category(activeCategory)
                .build();

        bookHiddenCategory = Book.builder()
                .id("book2")
                .status(0)
                .category(hiddenCategory)
                .build();
    }

    // Trường hợp thành công
    @Test
    void testUpdateBookStatus_Success() {
        when(bookRepository.findById("book1"))
                .thenReturn(Optional.of(bookActiveCategory));

        when(bookRepository.save(any(Book.class)))
                .thenReturn(bookActiveCategory);

        Book updated = bookService.updateBookStatus("book1", 1);

        assertEquals(1, updated.getStatus());
        verify(bookRepository).save(any(Book.class));
    }

    // Danh mục mà sách đang thuộc ẩn thì sách không được hiện
    @Test
    void testUpdateBookStatus_CategoryHidden() {
        when(bookRepository.findById("book2"))
                .thenReturn(Optional.of(bookHiddenCategory));

        assertThrows(IllegalStateException.class,
                () -> bookService.updateBookStatus("book2", 1));
    }

    // Sách không tìm thấy
    @Test
    void testUpdateBookStatus_NotFound() {
        when(bookRepository.findById("book404"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBookStatus("book404", 1));
    }
}
