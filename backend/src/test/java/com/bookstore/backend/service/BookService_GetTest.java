package com.bookstore.backend.service;

import com.bookstore.backend.dto.BookDTO;
import com.bookstore.backend.dto.BookDetailDTO;
import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookService_GetTest {

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

        private Book sampleBook;
        private ImageBook img1, img2;

        @BeforeEach
        void setup() {
                img1 = ImageBook.builder()
                                .id("img1")
                                .image("/uploads/books/1/img1.jpg")
                                .createdAt(LocalDateTime.now().minusDays(1))
                                .build();

                img2 = ImageBook.builder()
                                .id("img2")
                                .image("/uploads/books/1/img2.jpg")
                                .createdAt(LocalDateTime.now())
                                .build();

                Category category = Category.builder()
                                .id("cat1")
                                .name("Fiction")
                                .slug("fiction")
                                .status(1)
                                .build();

                sampleBook = Book.builder()
                                .id("book1")
                                .title("Sample Book")
                                .slug("sample-book")
                                .price(100.0)
                                .discount(20.0)
                                .status(1)
                                .stock(10)
                                .createdAt(LocalDateTime.now())
                                .author(Author.builder()
                                                .id("auth1")
                                                .fullname("John Doe")
                                                .slug("john-doe")
                                                .build())
                                .publisher(Publisher.builder()
                                                .id("pub1")
                                                .name("ABC Publisher")
                                                .slug("abc-pub")
                                                .build())
                                .category(category)
                                .images(List.of(img1, img2))
                                .build();
        }

        // Lấy tất cả sách
        @Test
        void testGetAllBooks_Success() {
                Page<Book> page = new PageImpl<>(List.of(sampleBook));

                when(bookRepository.findAll(any(Pageable.class)))
                                .thenReturn(page);
                when(orderDetailRepository.findTotalSoldByBook("book1"))
                                .thenReturn(50L);

                Page<BookDTO> result = bookService.getAllBooks(1, 10, null, null);

                assertEquals(1, result.getTotalElements());
                assertEquals("Sample Book", result.getContent().get(0).getTitle());
        }

        @Test
        void testGetAllBooks_WithSearch() {
                Page<Book> page = new PageImpl<>(List.of(sampleBook));

                when(bookRepository.searchByTitleAuthorPublisherCategory(eq("sample"), any(), any(Pageable.class)))
                                .thenReturn(page);

                when(orderDetailRepository.findTotalSoldByBook("book1"))
                                .thenReturn(20L);

                Page<BookDTO> result = bookService.getAllBooks(1, 10, "sample", null);

                assertEquals(1, result.getTotalElements());
                assertEquals(20L, result.getContent().get(0).getTotalSold());
        }

        // Lấy các sách có status = 1 (hiện)
        @Test
        void testGetAllActiveBooks_Success() {
                Page<Book> page = new PageImpl<>(List.of(sampleBook));

                when(bookRepository.findByStatusAndPriceRange(anyInt(), anyInt(), anyInt(), any(Pageable.class)))
                                .thenReturn(page);
                when(orderDetailRepository.findTotalSoldByBook("book1"))
                                .thenReturn(30L);

                Page<BookDTO> result = bookService.getAllActiveBooks(1, null, null, 0, 200);

                assertEquals(1, result.getTotalElements());
                assertEquals("Sample Book", result.getContent().get(0).getTitle());
        }

        // Lấy các sách giảm giá
        @Test
        void testGetDiscountedActiveBooks_Success() {
                Page<Book> page = new PageImpl<>(List.of(sampleBook));

                when(bookRepository.findDiscountedAndPriceRange(anyInt(), anyInt(), anyInt(), any(Pageable.class)))
                                .thenReturn(page);
                when(orderDetailRepository.findTotalSoldByBook("book1"))
                                .thenReturn(15L);

                Page<BookDTO> result = bookService.getDiscountedActiveBooks(1, null, null, 0, 200);

                assertEquals(1, result.getTotalElements());
                assertEquals(15L, result.getContent().get(0).getTotalSold());
        }

        // Lấy các sách theo danh mục
        @Test
        void testGetActiveBooksByCategory_Success() {
                Page<Book> page = new PageImpl<>(List.of(sampleBook));

                when(bookRepository.findByCategorySlugAndStatusAndPriceRange(eq("fiction"), anyInt(), anyInt(),
                                anyInt(), any(Pageable.class)))
                                .thenReturn(page);
                when(orderDetailRepository.findTotalSoldByBook("book1")).thenReturn(10L);

                Page<BookDTO> result = bookService.getActiveBooksByCategory("fiction", 1, null, null, 0, 200);

                assertEquals(1, result.getTotalElements());
                assertEquals("Sample Book", result.getContent().get(0).getTitle());
        }

        // Lấy sách theo tổng số đã bán
        @Test
        void testGetAllBooksByTotalSold_Success() {
                Page<Book> page = new PageImpl<>(List.of(sampleBook));

                when(bookRepository.findByStatusInOrderByTotalSold(anyList(), any(Pageable.class)))
                                .thenReturn(page);
                when(orderDetailRepository.findTotalSoldByBook("book1"))
                                .thenReturn(100L);

                List<BookDTO> result = bookService.getAllBooksByTotalSold();

                assertEquals(1, result.size());
        }

        @Test
        void testGetActiveBooksByTotalSold_Success() {
                Page<Book> page = new PageImpl<>(List.of(sampleBook));

                when(bookRepository.findByStatusInOrderByTotalSold(anyList(), any(Pageable.class)))
                                .thenReturn(page);
                when(orderDetailRepository.findTotalSoldByBook("book1"))
                                .thenReturn(22L);

                List<BookDTO> result = bookService.getActiveBooksByTotalSold();

                assertEquals(1, result.size());
        }

        // Lấy sách theo slug
        @Test
        void testGetBookBySlug_Success() {
                when(bookRepository.findBySlugAndStatus("sample-book", 1))
                                .thenReturn(Optional.of(sampleBook));

                BookDetailDTO result = bookService.getBookBySlug("sample-book");

                assertEquals("Sample Book", result.getTitle());
                assertEquals(2, result.getImages().size());
        }

        @Test
        void testGetBookBySlug_NotFound() {
                when(bookRepository.findBySlugAndStatus("x", 1))
                                .thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class,
                                () -> bookService.getBookBySlug("x"));
        }

        // Lấy sách theo id
        @Test
        void testGetBookById_Success() {
                when(bookRepository.findById("book1"))
                                .thenReturn(Optional.of(sampleBook));

                BookDetailDTO result = bookService.getBookById("book1");

                assertEquals("Sample Book", result.getTitle());
        }

        @Test
        void testGetBookById_NotFound() {
                when(bookRepository.findById("x"))
                                .thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class,
                                () -> bookService.getBookById("x"));
        }
}
