package com.bookstore.backend.controller;
import com.bookstore.backend.dto.BookDTO;
import com.bookstore.backend.dto.BookDetailDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
 @GetMapping
    public ResponseEntity<?> getAllBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer status
    ) {
        Page<BookDTO> bookPage = bookService.getAllBooks(page, limit, q, status);

        return ResponseEntity.ok(Map.of(
                "books", bookPage.getContent(),
                "totalPages", bookPage.getTotalPages(),
                "total", bookPage.getTotalElements()
        ));
    }

 @GetMapping("/active")
public ResponseEntity<?> getAllActiveBooks(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false) Integer min,
        @RequestParam(required = false) Integer max
) {
    Page<BookDTO> bookPage = bookService.getAllActiveBooks(page, q, sort, min, max);

    return ResponseEntity.ok(Map.of(
            "books", bookPage.getContent(),
            "totalPages", bookPage.getTotalPages(),
            "total", bookPage.getTotalElements()
    ));
}

@GetMapping("/active/discount")
public ResponseEntity<?> getDiscountedActiveBooks(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false) Integer min,
        @RequestParam(required = false) Integer max
) {
    Page<BookDTO> bookPage = bookService.getDiscountedActiveBooks(page, q, sort, min, max);

    return ResponseEntity.ok(Map.of(
            "books", bookPage.getContent(),
            "totalPages", bookPage.getTotalPages(),
            "total", bookPage.getTotalElements()
    ));
}

@GetMapping("/active/{slug}")
public ResponseEntity<?> getActiveBooksByCategory(
        @PathVariable("slug") String slug,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false) Integer min,
        @RequestParam(required = false) Integer max
) {
    Page<BookDTO> bookPage = bookService.getActiveBooksByCategory(slug, page, q, sort, min, max);

    return ResponseEntity.ok(Map.of(
            "books", bookPage.getContent(),
            "totalPages", bookPage.getTotalPages(),
            "total", bookPage.getTotalElements()
    ));
}

@GetMapping("/bestseller")
public ResponseEntity<?> getAllBooksByTotalSold() {
    List<BookDTO> bestsellerBooks = bookService.getAllBooksByTotalSold();
    return ResponseEntity.ok(Map.of("books", bestsellerBooks));
}

@GetMapping("/active/bestseller")
public ResponseEntity<?> getActiveBooksByTotalSold() {
    List<BookDTO> bestsellerBooks = bookService.getActiveBooksByTotalSold();
    return ResponseEntity.ok(Map.of("books", bestsellerBooks));
}


@GetMapping("/slug/{slug}")
public ResponseEntity<BookDetailDTO> getBookBySlug(@PathVariable String slug) {
    BookDetailDTO bookDetail = bookService.getBookBySlug(slug);
    return ResponseEntity.ok(bookDetail);
}

    @GetMapping("/{id}")
public ResponseEntity<BookDetailDTO> getBookById(@PathVariable String id) {
    BookDetailDTO bookDetail = bookService.getBookById(id);
    return ResponseEntity.ok(bookDetail);
}

@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Book> createBook(
        @RequestPart("book") Book book,
        @RequestPart(value = "files", required = true) List<MultipartFile> files
) {
    Book savedBook = bookService.createBook(book, files);
    return ResponseEntity.ok(savedBook);
}

@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Book> updateBook(
        @PathVariable String id,
        @RequestPart("book") Book book,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
) {
    Book updatedBook = bookService.updateBook(id, book, files);
    return ResponseEntity.ok(updatedBook);
}

    @PatchMapping("/status/{id}")
    public ResponseEntity<?> updateBookStatus(
            @PathVariable String id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer status = body.get("status");
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        Book updated = bookService.updateBookStatus(id, status);

        return ResponseEntity.ok(Map.of(
                "id", updated.getId(),
                "status", updated.getStatus()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // image book
@PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> updateImagesBook(
        @RequestParam(value = "oldImageIds", required = false) List<String> oldImageIds,
        @RequestPart(value = "files", required = false) List<MultipartFile> files
) {
    bookService.updateImagesBook(files, oldImageIds);
    return ResponseEntity.noContent().build();
}

       @DeleteMapping("/image/{imageId}")
    public ResponseEntity<Void> deleteImageBook(@PathVariable String imageId) {
        bookService.deleteImageBook(imageId);
        return ResponseEntity.noContent().build();
    }

}
