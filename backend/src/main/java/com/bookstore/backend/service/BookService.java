package com.bookstore.backend.service;

import com.bookstore.backend.dto.BookDTO;
import com.bookstore.backend.dto.BookDetailDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.ImageBook;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartItemRepository;
import com.bookstore.backend.repository.ImageBookRepository;
import com.bookstore.backend.repository.OrderDetailRepository;
import com.bookstore.backend.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final String UPLOAD_DIR = "uploads/books/";
    private final BookRepository bookRepository;
    private final ImageBookRepository imageBookRepository;
    private final OrderDetailRepository orderDetailRepository;
        private final CartItemRepository cartItemRepository;
    public BookService(BookRepository bookRepository, ImageBookRepository imageBookRepository, OrderDetailRepository orderDetailRepository, CartItemRepository cartItemRepository) {
        this.bookRepository = bookRepository;
        this.imageBookRepository = imageBookRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartItemRepository = cartItemRepository;
    }

public List<BookDTO> getAllBooks() {
    List<Book> books = bookRepository.findAll();
    return books.stream().map(book -> {
        List<String> imagePaths = book.getImages() != null 
            ? book.getImages().stream()
                  .map(ImageBook::getImage)
                  .toList()
            : List.of(); 

        return new BookDTO(
            book.getId(),
            book.getTitle(),
            imagePaths,
            book.getPrice(),
            book.getDiscount(),
            book.getSlug(),
            book.getAuthor().getFullname(),
            book.getPublisher().getName(),
            book.getCategory().getName(),
            book.getStock(),
            book.getStatus()
        );
    }).collect(Collectors.toList());
}

public Optional<BookDetailDTO> getBookById(String id) {
    return bookRepository.findById(id)
            .map(book -> {
                List<String> imagePaths = book.getImages() != null 
                        ? book.getImages().stream()
                            .map(ImageBook::getImage)
                            .toList()
                        : List.of();

                return new BookDetailDTO(
                        book.getId(),
                        book.getTitle(),
                        imagePaths,
                        book.getPrice(),
                        book.getDiscount(),
                        book.getDescription(),
                        book.getSlug(),
                        book.getAuthor().getFullname(),
                        book.getCategory().getName(),
                        book.getNumberOfPages(),
                        book.getWeight(),
                        book.getWidth(),
                        book.getLength(),
                        book.getThickness(),
                        book.getStock(),
                        book.getStatus()
                );
            });
}


public Book createBook(Book book, List<MultipartFile> files) throws IOException {
    if (bookRepository.findByTitle(book.getTitle()).isPresent()) {
        throw new IllegalArgumentException("Book title already exists");
    }

    book.setSlug(SlugUtil.toSlug(book.getTitle()));

    Book savedBook = bookRepository.save(book);

File bookDir = new File(UPLOAD_DIR + book.getSlug());
    if (!bookDir.exists()) {
        bookDir.mkdirs();
    }

     if (files != null && !files.isEmpty()) {
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            File dest = new File(bookDir, filename);
            file.transferTo(dest);

            ImageBook imageBook = ImageBook.builder()
                    .image(book.getSlug() + "/" + filename)
                    .book(savedBook)
                    .build();
            imageBookRepository.save(imageBook);
        }
    }

    return savedBook;
}


public Book updateBook(String id, Book book, List<MultipartFile> newFiles) throws IOException {
    return bookRepository.findById(id)
            .map(existingBook -> {
                bookRepository.findByTitle(book.getTitle())
                        .filter(b -> !b.getId().equals(id))
                        .ifPresent(b -> { throw new IllegalArgumentException("Book title already exists"); });

                book.setSlug(SlugUtil.toSlug(book.getTitle()));

                existingBook.setTitle(book.getTitle());
                existingBook.setSlug(book.getSlug());
                existingBook.setPrice(book.getPrice());
                existingBook.setDiscount(book.getDiscount());
                existingBook.setDescription(book.getDescription());
                existingBook.setPublicationDate(book.getPublicationDate());
                existingBook.setNumberOfPages(book.getNumberOfPages());
                existingBook.setWeight(book.getWeight());
                existingBook.setWidth(book.getWidth());
                existingBook.setLength(book.getLength());
                existingBook.setThickness(book.getThickness());
                existingBook.setStatus(book.getStatus());
                existingBook.setStock(book.getStock());
                existingBook.setCategory(book.getCategory());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPublisher(book.getPublisher());

                Book updatedBook = bookRepository.save(existingBook);

                File bookDir = new File(UPLOAD_DIR + book.getSlug());
                if (!bookDir.exists()) {
                    bookDir.mkdirs();
                }

                if (newFiles != null && !newFiles.isEmpty()) {
                    for (MultipartFile file : newFiles) {
                        String filename = file.getOriginalFilename();
                        File dest = new File(bookDir, filename);
                        try {
                            file.transferTo(dest);
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to save image", e);
                        }

                        ImageBook imageBook = ImageBook.builder()
                                .image(book.getSlug() + "/" + filename)
                                .book(updatedBook)
                                .build();
                        imageBookRepository.save(imageBook);
                    }
                }

                return updatedBook;
            })
            .orElse(null);
}

    public void deleteBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found book"));

   if (orderDetailRepository.existsByBook(book)) {
        throw new IllegalStateException("This book cannot be deleted because it has been purchased in an order");
    }

        if (cartItemRepository.existsByBook(book)) {
        throw new IllegalStateException("This book cannot be deleted because it is in user carts");
    }

       List<ImageBook> images = imageBookRepository.findByBook(book);
    imageBookRepository.deleteAll(images);

    File bookDir = new File(UPLOAD_DIR + book.getSlug());
    if (bookDir.exists() && bookDir.isDirectory()) {
        for (File file : bookDir.listFiles()) {
            file.delete(); 
        }
        bookDir.delete(); 
    }

        bookRepository.deleteById(id);
    }
}
