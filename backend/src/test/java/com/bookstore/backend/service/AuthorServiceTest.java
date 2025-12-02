package com.bookstore.backend.service;

import com.bookstore.backend.entities.Author;
import com.bookstore.backend.repository.AuthorRepository;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private Author updatedAuthor;

    @BeforeEach
    void setup() {
        author = Author.builder()
                .id("a1")
                .fullname("John Wick")
                .slug("john-wick")
                .createdAt(LocalDateTime.now())
                .build();

        updatedAuthor = Author.builder()
                .fullname("John Updated")
                .build();
    }

    // =========================================================
    // getAllAuthors
    // =========================================================

    @Test
    void testGetAllAuthors_NoQuery() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Author> pageResult = new PageImpl<>(List.of(author));

        when(authorRepository.findAll(pageable)).thenReturn(pageResult);

        Page<Author> result = authorService.getAllAuthors(1, 10, null);

        assertEquals(1, result.getTotalElements());
        verify(authorRepository).findAll(pageable);
    }

    @Test
    void testGetAllAuthors_WithQuery() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Author> pageResult = new PageImpl<>(List.of(author));

        when(authorRepository.findByFullnameContainingIgnoreCase("john", pageable))
                .thenReturn(pageResult);

        Page<Author> result = authorService.getAllAuthors(1, 10, "john");

        assertEquals(1, result.getTotalElements());
        verify(authorRepository).findByFullnameContainingIgnoreCase("john", pageable);
    }

    // =========================================================
    // getAllAuthors1
    // =========================================================

    @Test
    void testGetAllAuthors1() {
        when(authorRepository.findAll(Sort.by("createdAt").descending()))
                .thenReturn(List.of(author));

        List<Author> result = authorService.getAllAuthors1();

        assertEquals(1, result.size());
        verify(authorRepository).findAll(any(Sort.class));
    }

    // =========================================================
    // getAuthorById
    // =========================================================

    @Test
    void testGetAuthorById_Success() {
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));

        Author result = authorService.getAuthorById("a1");

        assertEquals("a1", result.getId());
    }

    @Test
    void testGetAuthorById_NotFound() {
        when(authorRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> authorService.getAuthorById("missing"));
    }

    // =========================================================
    // createAuthor
    // =========================================================

    @Test
    void testCreateAuthor_NameExists() {
        when(authorRepository.findByFullname("John Wick")).thenReturn(Optional.of(author));

        assertThrows(IllegalArgumentException.class,
                () -> authorService.createAuthor(author));
    }

    @Test
    void testCreateAuthor_Success() {
        try (MockedStatic<SlugUtil> mocked = mockStatic(SlugUtil.class)) {

            mocked.when(() -> SlugUtil.toSlug("John Wick")).thenReturn("john-wick");

            when(authorRepository.findByFullname("John Wick")).thenReturn(Optional.empty());
            when(authorRepository.save(any(Author.class))).thenReturn(author);

            Author result = authorService.createAuthor(author);

            assertEquals("john-wick", result.getSlug());
            verify(authorRepository).save(any(Author.class));
        }
    }

    // =========================================================
    // updateAuthor
    // =========================================================

    @Test
    void testUpdateAuthor_NotFound() {
        when(authorRepository.findById("missing")).thenReturn(Optional.empty());

        Author result = authorService.updateAuthor("missing", updatedAuthor);

        assertNull(result);
    }

    @Test
    void testUpdateAuthor_NameExistsForAnotherAuthor() {
        Author otherAuthor = Author.builder()
                .id("a2")
                .fullname("John Updated")
                .build();

        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(authorRepository.findByFullname("John Updated"))
                .thenReturn(Optional.of(otherAuthor));

        assertThrows(IllegalArgumentException.class,
                () -> authorService.updateAuthor("a1", updatedAuthor));
    }

    @Test
    void testUpdateAuthor_Success() {
        try (MockedStatic<SlugUtil> mocked = mockStatic(SlugUtil.class)) {

            mocked.when(() -> SlugUtil.toSlug("John Updated")).thenReturn("john-updated");

            when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
            when(authorRepository.findByFullname("John Updated"))
                    .thenReturn(Optional.empty());
            when(authorRepository.save(author)).thenReturn(author);

            Author result = authorService.updateAuthor("a1", updatedAuthor);

            assertEquals("John Updated", result.getFullname());
            assertEquals("john-updated", result.getSlug());
        }
    }

    // =========================================================
    // deleteAuthor
    // =========================================================

    @Test
    void testDeleteAuthor_NotFound() {
        when(authorRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> authorService.deleteAuthor("missing"));
    }

    @Test
    void testDeleteAuthor_HasBooks() {
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(bookRepository.existsByAuthor(author)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> authorService.deleteAuthor("a1"));
    }

    @Test
    void testDeleteAuthor_Success() {
        when(authorRepository.findById("a1")).thenReturn(Optional.of(author));
        when(bookRepository.existsByAuthor(author)).thenReturn(false);

        authorService.deleteAuthor("a1");

        verify(authorRepository).deleteById("a1");
    }
}
