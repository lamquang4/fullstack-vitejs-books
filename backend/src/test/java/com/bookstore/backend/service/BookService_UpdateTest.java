package com.bookstore.backend.service;

import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookService_UpdateTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private ImageBookRepository imageBookRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    private Book existingBook;
    private Book updatedBook;
    private Category activeCategory;

    @BeforeEach
    void setup() {
        activeCategory = Category.builder()
                .id("cat1")
                .status(1)
                .name("Education")
                .build();

        existingBook = Book.builder()
                .id("book1")
                .title("Old Title")
                .price(100.0)
                .discount(10.0)
                .stock(10)
                .numberOfPages(300)
                .weight(200.0)
                .width(10.0)
                .length(20.0)
                .thickness(5.0)
                .category(activeCategory)
                .build();

        updatedBook = Book.builder()
                .title("New Title")
                .price(200.0)
                .discount(20.0)
                .stock(5)
                .numberOfPages(250)
                .weight(300.0)
                .width(12.0)
                .length(25.0)
                .thickness(6.0)
                .category(activeCategory)
                .build();
    }

    // Trường hợp thành công
    void testUpdateBook_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("abc.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        doNothing().when(file).transferTo(any(File.class));

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any())).thenReturn(existingBook);

        Book result = bookService.updateBook("book1", updatedBook, List.of(file));

        assertEquals(updatedBook.getTitle(), result.getTitle());
        verify(bookRepository).save(any(Book.class));
        verify(imageBookRepository).save(any());
    }

    // Danh mục không tìm thấy (service KHÔNG check → test xác minh rằng không ném
    // exception)
    @Test
    void testUpdateBook_CategoryNotFound_NoCheckInService() {
        updatedBook.setCategory(Category.builder().id("catX").build());

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        // service hiện tại KHÔNG gọi categoryRepository → KHÔNG throw
        assertDoesNotThrow(() -> bookService.updateBook("book1", updatedBook, null));
    }

    // Lỗi truyền file
    @Test
    void testUpdateBook_FileTransferException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("abc.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");

        doThrow(new IOException("FAIL"))
                .when(file).transferTo(any(File.class));

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        assertThrows(RuntimeException.class,
                () -> bookService.updateBook("book1", updatedBook, List.of(file)));
    }

    // Giá không hợp lệ
    @Test
    void testUpdateBook_InvalidPrice() {
        updatedBook.setPrice(0.0);

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("book1", updatedBook, null));
    }

    // Giảm giá không hợp lệ
    @Test
    void testUpdateBook_InvalidDiscountNegative() {
        updatedBook.setDiscount(-1.0);

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("book1", updatedBook, null));
    }

    // Giảm giá không hợp lệ — mức giảm quá lớn
    @Test
    void testUpdateBook_InvalidDiscountTooLarge() {
        updatedBook.setDiscount(999.0);

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("book1", updatedBook, null));
    }

    // Số lượng hiện có không hợp lệ
    @Test
    void testUpdateBook_InvalidStock() {
        updatedBook.setStock(-5);

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("book1", updatedBook, null));
    }

    // Phần mở rộng không hợp lệ
    @Test
    void testUpdateBook_InvalidFileExtension() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("xxx.exe");

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("book1", updatedBook, List.of(file)));
    }

    // Kiểu MIME không hợp lệ
    @Test
    void testUpdateBook_InvalidMimeType() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("img.png");
        when(file.getContentType()).thenReturn("application/octet-stream");

        when(bookRepository.findById("book1")).thenReturn(Optional.of(existingBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateBook("book1", updatedBook, List.of(file)));
    }
}
