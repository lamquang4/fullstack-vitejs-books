package com.bookstore.backend.controller;

import com.bookstore.backend.dto.BookDTO;
import com.bookstore.backend.dto.BookDetailDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.service.BookService;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
public List<BookDTO> getAllBooks() {
    return bookService.getAllBooks();
}

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailDTO> getBookById(@PathVariable String id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

@PostMapping
    public ResponseEntity<Book> createBook(
            @RequestPart("book") Book book,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Book created = bookService.createBook(book, images);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable String id,
            @RequestPart("book") Book book,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        Book updated = bookService.updateBook(id, book, images);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
