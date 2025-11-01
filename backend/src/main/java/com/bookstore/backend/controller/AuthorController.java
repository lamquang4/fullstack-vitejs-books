package com.bookstore.backend.controller;
import com.bookstore.backend.entities.Author;
import com.bookstore.backend.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/author")
@CrossOrigin(origins = "*")
public class AuthorController {

    private final AuthorService authorService;
    
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<?> getAllAuthors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String q
    ) {
        Page<Author> authorPage = authorService.getAllAuthors(page, limit, q);

        return ResponseEntity.ok(Map.of(
            "authors", authorPage.getContent(),
            "totalPages", authorPage.getTotalPages(),
            "total", authorPage.getTotalElements()
        ));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Author>> getAllAuthors1() {
        List<Author> authors = authorService.getAllAuthors1();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable String id) {
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    @PostMapping
    public Author createAuthor(@RequestBody Author author) {
        return authorService.createAuthor(author);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable String id, @RequestBody Author author) {
        Author updated = authorService.updateAuthor(id, author);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable String id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}

