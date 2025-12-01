package com.bookstore.backend.service;

import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookService_CreateTest {

    @Mock private BookRepository bookRepository;
    @Mock private ImageBookRepository imageBookRepository;
    @Mock private OrderDetailRepository orderDetailRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private BookService bookService;

    private Book validBook;
    private Category activeCategory;

    @BeforeEach
    void setup() {

        activeCategory = Category.builder()
                .id("cat1")
                .name("Category A")
                .status(1)
                .build();

        validBook = Book.builder()
                .id("book1")
                .title("New Book")
                .price(100.0)
                .discount(10.0)
                .numberOfPages(200)
                .weight(300.0)
                .width(10.0)
                .length(20.0)
                .thickness(5.0)
                .stock(5)
                .category(activeCategory)
                .build();
    }

    // Trường hợp thành công
    @Test
    void testCreateBook_Success() throws IOException {

        when(categoryRepository.findById("cat1"))
                .thenReturn(Optional.of(activeCategory));

        when(bookRepository.findByTitle("New Book"))
                .thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book saved = inv.getArgument(0);
            saved.setId("book123");
            return saved;
        });

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");

        Book result = bookService.createBook(validBook, List.of(file));

        assertNotNull(result.getId());
        verify(bookRepository).save(any(Book.class));
        verify(imageBookRepository).save(any(ImageBook.class));
    }

    // Trùng tiêu đề
    @Test
    void testCreateBook_TitleDuplicate() {

        when(bookRepository.findByTitle("New Book"))
                .thenReturn(Optional.of(validBook));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, List.of()));
    }

    // Lỗi xác thực
    @Test
    void testCreateBook_InvalidPrice() {
        validBook.setPrice(0.0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidDiscountNegative() {
        validBook.setDiscount(-1.0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidDiscountOverPrice() {
        validBook.setDiscount(500.0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidStock() {
        validBook.setStock(-10);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidPages() {
        validBook.setNumberOfPages(0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidWeight() {
        validBook.setWeight(0.0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidWidth() {
        validBook.setWidth(0.0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidLength() {
        validBook.setLength(0.0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    @Test
    void testCreateBook_InvalidThickness() {
        validBook.setThickness(0.0);

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, null));
    }

    // Danh mục không tìm thấy
    @Test
    void testCreateBook_CategoryNotFound() {

        when(categoryRepository.findById("cat1"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.createBook(validBook, null));
    }

    // Danh mục mà sách thuộc bị ẩn thì các sách thuộc danh mục sẽ bị ẩn
    @Test
    void testCreateBook_CategoryHidden_ForceBookStatusZero() {

        Category hiddenCategory = Category.builder()
                .id("cat1")
                .status(0)
                .build();

        validBook.setCategory(hiddenCategory);

        when(categoryRepository.findById("cat1"))
                .thenReturn(Optional.of(hiddenCategory));

        when(bookRepository.findByTitle("New Book"))
                .thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(inv -> {
                    Book b = inv.getArgument(0);
                    b.setId("book123");
                    return b;
                });

        Book result = bookService.createBook(validBook, List.of());

        assertEquals(0, result.getStatus());
    }

    // Phần mở rộng không hợp lệ
    @Test
    void testCreateBook_InvalidFileExtension() {

        when(categoryRepository.findById("cat1"))
                .thenReturn(Optional.of(activeCategory));

        when(bookRepository.findByTitle("New Book"))
                .thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId("book999");
            return b;
        });

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.exe");

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, List.of(file)));
    }

    // Kiểu MIME không hợp lệ
    @Test
    void testCreateBook_InvalidMimeType() {

        when(categoryRepository.findById("cat1"))
                .thenReturn(Optional.of(activeCategory));

        when(bookRepository.findByTitle("New Book"))
                .thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId("book999");
            return b;
        });

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getContentType()).thenReturn("application/octet-stream");

        assertThrows(IllegalArgumentException.class,
                () -> bookService.createBook(validBook, List.of(file)));
    }

    // Lỗi truyền file
    @Test
    void testCreateBook_FileTransferError() throws IOException {

        when(categoryRepository.findById("cat1"))
                .thenReturn(Optional.of(activeCategory));

        when(bookRepository.findByTitle("New Book"))
                .thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(inv -> {
                    Book b = inv.getArgument(0);
                    b.setId("book999");
                    return b;
                });

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");

        doThrow(new IOException("fail"))
                .when(file).transferTo(any(File.class));

        assertThrows(RuntimeException.class,
                () -> bookService.createBook(validBook, List.of(file)));
    }
}
