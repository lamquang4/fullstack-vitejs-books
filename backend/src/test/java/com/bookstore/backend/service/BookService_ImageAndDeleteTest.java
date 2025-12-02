package com.bookstore.backend.service;

import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.ImageBook;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartItemRepository;
import com.bookstore.backend.repository.ImageBookRepository;
import com.bookstore.backend.repository.OrderDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@MockitoSettings(strictness = Strictness.LENIENT)
class BookService_ImageAndDeleteTest {

    @Mock private BookRepository bookRepository;
    @Mock private ImageBookRepository imageBookRepository;
    @Mock private OrderDetailRepository orderDetailRepository;
    @Mock private CartItemRepository cartItemRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setup() {
        Category category = Category.builder()
                .id("cat1")
                .status(1)
                .build();

        book = Book.builder()
                .id("book1")
                .title("Test Book")
                .category(category)
                .build();
    }

    // =========================================================
    // DELETE BOOK TESTS
    // =========================================================

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));
        when(orderDetailRepository.existsByBook(book)).thenReturn(false);
        when(cartItemRepository.existsByBook(book)).thenReturn(false);
        when(imageBookRepository.findByBook(book)).thenReturn(List.of());

        assertDoesNotThrow(() -> bookService.deleteBook("book1"));
        verify(bookRepository).deleteById("book1");
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.findById("book1")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteBook("book1"));
    }

    @Test
    void testDeleteBook_InOrder() {
        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));
        when(orderDetailRepository.existsByBook(book)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> bookService.deleteBook("book1"));
    }

    @Test
    void testDeleteBook_InCart() {
        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));
        when(orderDetailRepository.existsByBook(book)).thenReturn(false);
        when(cartItemRepository.existsByBook(book)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> bookService.deleteBook("book1"));
    }

    @Test
    void testDeleteBook_DeleteImagesSuccess() {
        ImageBook img = ImageBook.builder()
                .id("img1")
                .image("/uploads/books/book1/old.jpg")
                .book(book)
                .build();

        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));
        when(orderDetailRepository.existsByBook(book)).thenReturn(false);
        when(cartItemRepository.existsByBook(book)).thenReturn(false);
        when(imageBookRepository.findByBook(book)).thenReturn(List.of(img));

        assertDoesNotThrow(() -> bookService.deleteBook("book1"));
        verify(imageBookRepository).deleteAll(anyList());
        verify(bookRepository).deleteById("book1");
    }

    // =========================================================
    // UPDATE IMAGES TESTS
    // =========================================================

    @Test
    void testUpdateImagesBook_DoNothing_WhenEmptyLists() {
        assertDoesNotThrow(() -> bookService.updateImagesBook(List.of(), List.of()));
    }

    @Test
    void testUpdateImagesBook_InvalidExtension() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("invalid.exe");

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateImagesBook(List.of(file), List.of("img1")));
    }

    @Test
    void testUpdateImagesBook_InvalidMime() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("binary/octet-stream");

        assertThrows(IllegalArgumentException.class,
                () -> bookService.updateImagesBook(List.of(file), List.of("img1")));
    }

    @Test
    void testUpdateImagesBook_ImageNotFound() {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(imageBookRepository.findById("img1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateImagesBook(List.of(file), List.of("img1")));
    }

    @Test
    void testUpdateImagesBook_IOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        ImageBook img = ImageBook.builder()
                .id("img1")
                .image("/uploads/books/book1/test.jpg")
                .build();

        lenient().when(file.getOriginalFilename()).thenReturn("test.jpg");
        lenient().when(file.getContentType()).thenReturn("image/jpeg");
        when(imageBookRepository.findById("img1")).thenReturn(Optional.of(img));

        // mô phỏng lỗi khi ghi file
        doThrow(new IOException("FAIL")).when(file).transferTo(any(File.class));

        assertThrows(RuntimeException.class,
                () -> bookService.updateImagesBook(List.of(file), List.of("img1")));
    }

    @Test
    void testUpdateImagesBook_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        // 1️⃣ user.dir
        String baseDir = System.getProperty("user.dir");

        // 2️⃣ tạo folder đúng như service cần
        File uploadsDir = new File(baseDir + "/uploads/books/book1");
        uploadsDir.mkdirs();

        // 3️⃣ tạo file thật
        File temp = new File(uploadsDir, "test.jpg");
        if (!temp.exists()) {
            temp.createNewFile();
        }

        // 4️⃣ mock ImageBook.image là đường dẫn TƯƠNG ĐỐI (giống lúc lưu thật)
        ImageBook img = ImageBook.builder()
                .id("img1")
                .image("/uploads/books/book1/test.jpg") // MUST BE RELATIVE
                .build();

        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(imageBookRepository.findById("img1")).thenReturn(Optional.of(img));

        // 5️⃣ mock transferTo ghi đè file thật
        doAnswer(inv -> {
            File dest = inv.getArgument(0);
            assertTrue(dest.exists()); // file thật tồn tại
            return null;
        }).when(file).transferTo(any(File.class));

        assertDoesNotThrow(() ->
                bookService.updateImagesBook(List.of(file), List.of("img1"))
        );
    }

    // =========================================================
    // DELETE IMAGE TESTS
    // =========================================================

    @Test
    void testDeleteImageBook_Success() {
        ImageBook img = ImageBook.builder()
                .id("img1")
                .image("/uploads/books/book1/img.jpg")
                .build();

        when(imageBookRepository.findById("img1")).thenReturn(Optional.of(img));

        assertDoesNotThrow(() -> bookService.deleteImageBook("img1"));
        verify(imageBookRepository).delete(img);
    }

    @Test
    void testDeleteImageBook_NotFound() {
        when(imageBookRepository.findById("img404")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookService.deleteImageBook("img404"));
    }

    @Test
    void testDeleteImageBook_FileNotFound() {
        ImageBook img = ImageBook.builder()
                .id("img1")
                .image("/uploads/books/book1/notfound.jpg")
                .build();

        when(imageBookRepository.findById("img1")).thenReturn(Optional.of(img));

        assertDoesNotThrow(() -> bookService.deleteImageBook("img1"));
        verify(imageBookRepository).delete(img);
    }
}
