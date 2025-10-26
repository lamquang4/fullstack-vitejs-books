package com.bookstore.backend.service;
import com.bookstore.backend.dto.AuthorDTO;
import com.bookstore.backend.dto.BookDTO;
import com.bookstore.backend.dto.BookDetailDTO;
import com.bookstore.backend.dto.CategoryDTO;
import com.bookstore.backend.dto.ImageBookDTO;
import com.bookstore.backend.dto.PublisherDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.ImageBook;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.CartItemRepository;
import com.bookstore.backend.repository.ImageBookRepository;
import com.bookstore.backend.repository.OrderDetailRepository;
import org.springframework.transaction.annotation.Transactional;
import com.bookstore.backend.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections; 
import java.util.Comparator;
import java.util.Arrays;
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

private BookDTO convertToDTO(Book book) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    BookDTO dto = new BookDTO();
    dto.setId(book.getId());
    dto.setTitle(book.getTitle());
    dto.setPrice(book.getPrice());
    dto.setDiscount(book.getDiscount());
    dto.setSlug(book.getSlug());
    dto.setStock(book.getStock());
    dto.setStatus(book.getStatus());

    if (book.getAuthor() != null) {
        dto.setAuthor(new AuthorDTO(
            book.getAuthor().getId(),
                book.getAuthor().getFullname(),
                book.getAuthor().getSlug()
        ));
    }

    if (book.getPublisher() != null) {
        dto.setPublisher(new PublisherDTO(
             book.getPublisher().getId(),
                book.getPublisher().getName(),
                book.getPublisher().getSlug()
        ));
    }

    if (book.getCategory() != null) {
        dto.setCategory(new CategoryDTO(
            book.getCategory().getId(),
                book.getCategory().getName(),
                book.getCategory().getSlug()
        ));
    }

    if (book.getImages() != null && !book.getImages().isEmpty()) {
        List<ImageBookDTO> imageDTOs = book.getImages().stream()
                .sorted(Comparator.comparing(img -> img.getCreatedAt()))
                .map(img -> new ImageBookDTO(
                        img.getId(),
                        img.getImage(),
                        img.getCreatedAt() != null ? img.getCreatedAt().format(formatter) : null
                ))
                .collect(Collectors.toList());
        dto.setImages(imageDTOs);
    } else {
        dto.setImages(Collections.emptyList());
    }

    Long totalSold = orderDetailRepository.findTotalSoldByBook(book.getId());
    dto.setTotalSold(totalSold != null ? totalSold : 0L);

    return dto;
}


// lấy tất cả sách
public Page<BookDTO> getAllBooks(int page, int limit, String q, Integer status) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

    Page<Book> bookPage;
    if (q != null && !q.isEmpty()) {
        bookPage = bookRepository.searchByTitleAuthorPublisherCategory(q, status, pageable);
    } else if (status != null) {
        bookPage = bookRepository.findByStatus(status, pageable);
    } else {
        bookPage = bookRepository.findAll(pageable);
    }

    return bookPage.map(this::convertToDTO);
}


// lây sách có status = 1
public Page<BookDTO> getAllActiveBooks(int page, String q, String sort) {
    int limit = 12;
    int status = 1;
    Pageable pageable = PageRequest.of(page - 1, limit);

    Page<Book> bookPage;
if (q != null && !q.isEmpty()) {
    bookPage = bookRepository.searchByTitleAuthorPublisherCategory(q, status, pageable);
} else {
    if (sort == null || sort.isEmpty()) {
        bookPage = bookRepository.findByStatus(status, pageable);
    } else {
        switch (sort) {
            case "price-asc":
                bookPage = bookRepository.findByStatusOrderByEffectivePriceAsc(status, pageable);
                break;
            case "price-desc":
                bookPage = bookRepository.findByStatusOrderByEffectivePriceDesc(status, pageable);
                break;
            case "bestseller":
                bookPage = bookRepository.findByStatusOrderByTotalSold(status, pageable);
                break;
            default:
                bookPage = bookRepository.findByStatus(status, pageable);
        }
    }
}

    return bookPage.map(this::convertToDTO);
}

// lây sách có status = 1 và giảm giá 
public Page<BookDTO> getDiscountedActiveBooks(int page, String q, String sort) {
    int limit = 12;
    int status = 1;
    Pageable pageable = PageRequest.of(page - 1, limit);

    Page<Book> bookPage;
if (q != null && !q.isEmpty()) {
    bookPage = bookRepository.searchDiscountedActiveCategory(q, status, pageable);
} else {
    if (sort == null || sort.isEmpty()) {
        bookPage = bookRepository.findDiscounted(status, pageable);
    } else {
        switch (sort) {
            case "price-asc":
                bookPage = bookRepository.findDiscountedOrderByEffectivePriceAsc(status, pageable);
                break;
            case "price-desc":
                bookPage = bookRepository.findDiscountedOrderByEffectivePriceDesc(status, pageable);
                break;
            case "bestseller":
                bookPage = bookRepository.findDiscountedOrderByTotalSold(status, pageable);
                break;
            default:
                bookPage = bookRepository.findDiscounted(status, pageable);
        }
    }
}

    return bookPage.map(this::convertToDTO);
}

// lấy sách có status = 1 theo category
public Page<BookDTO> getActiveBooksByCategory(String slug, int page, String q, String sort) {
    int limit = 12;
    int status = 1;
    Pageable pageable = PageRequest.of(page - 1, limit);

    Page<Book> bookPage;
if (q != null && !q.isEmpty()) {
    bookPage = bookRepository.searchByCategoryAndTitleAuthorPublisherCategory(slug, q, status, pageable);
} else {
    if (sort == null || sort.isEmpty()) {
        bookPage = bookRepository.findByCategorySlugAndStatus(slug, status, pageable);
    } else {
        switch (sort) {
            case "price-asc":
                bookPage = bookRepository.findByCategorySlugAndStatusOrderByEffectivePriceAsc(slug, status, pageable);
                break;
            case "price-desc":
                bookPage = bookRepository.findByCategorySlugAndStatusOrderByEffectivePriceDesc(slug, status, pageable);
                break;
            case "bestseller":
                bookPage = bookRepository.findByCategorySlugAndStatusOrderByTotalSold(slug, status, pageable);
                break;
            default:
                bookPage = bookRepository.findByCategorySlugAndStatus(slug, status, pageable);
        }
    }
}

    return bookPage.map(this::convertToDTO);
}

// lấy tất cả sách bestseller
public List<BookDTO> getAllBooksByTotalSold() {
 List<Integer> statuses = Arrays.asList(0, 1);
    int limit = 12;
    Pageable top = PageRequest.of(0, limit);
    List<Book> books = bookRepository.findByStatusInOrderByTotalSold(statuses, top).getContent();
    return books.stream().map(this::convertToDTO).collect(Collectors.toList());
}


// lấy sách có status là 1 và bestseller 
public List<BookDTO> getActiveBooksByTotalSold() {
 List<Integer> statuses = Arrays.asList(1);
    int limit = 12;
    Pageable top = PageRequest.of(0, limit);
List<Book> books = bookRepository.findByStatusInOrderByTotalSold(statuses, top).getContent();
    return books.stream().map(this::convertToDTO).collect(Collectors.toList());
}

// lấy sách theo slug có status là 1
public BookDetailDTO getBookBySlug(String slug) {
    Book book = bookRepository.findBySlugAndStatus(slug, 1)
            .orElseThrow(() -> new EntityNotFoundException("Book not found"));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    AuthorDTO authorDTO = null;
    if (book.getAuthor() != null) {
        authorDTO = new AuthorDTO(
            book.getAuthor().getId(),
                book.getAuthor().getFullname(),
                book.getAuthor().getSlug()
        );
    }

    PublisherDTO publisherDTO = null;
    if (book.getPublisher() != null) {
        publisherDTO = new PublisherDTO(
            book.getPublisher().getId(),
                book.getPublisher().getName(),
                book.getPublisher().getSlug()
        );
    }

    CategoryDTO categoryDTO = null;
    if (book.getCategory() != null) {
        categoryDTO = new CategoryDTO(
             book.getCategory().getId(),
                book.getCategory().getName(),
                book.getCategory().getSlug()
        );
    }

    List<ImageBookDTO> imageDTOs = (book.getImages() != null && !book.getImages().isEmpty())
            ? book.getImages().stream()
                .sorted((i1, i2) -> i1.getCreatedAt().compareTo(i2.getCreatedAt()))
                .map(img -> new ImageBookDTO(
                        img.getId(),
                        img.getImage(),
                        img.getCreatedAt() != null ? img.getCreatedAt().format(formatter) : null
                ))
                .collect(Collectors.toList())
            : Collections.emptyList();

    return new BookDetailDTO(
            book.getId(),
            book.getTitle(),
            book.getPrice(),
            book.getDiscount(),
            book.getDescription(),
            book.getSlug(),
            book.getNumberOfPages(),
            book.getPublicationDate(),
            book.getWeight(),
            book.getWidth(),
            book.getLength(),
            book.getThickness(),
            book.getStock(),
            book.getStatus(),
            book.getCreatedAt() != null ? book.getCreatedAt().format(formatter) : null,
            authorDTO,
            publisherDTO,
            categoryDTO,
            imageDTOs
    );
}

// lấy sách theo id
public BookDetailDTO getBookById(String id) {
    Book book = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book not found with ID: " + id));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    AuthorDTO authorDTO = null;
    if (book.getAuthor() != null) {
        authorDTO = new AuthorDTO(
            book.getAuthor().getId(),
                book.getAuthor().getFullname(),
                book.getAuthor().getSlug()
        );
    }

    PublisherDTO publisherDTO = null;
    if (book.getPublisher() != null) {
        publisherDTO = new PublisherDTO(
            book.getPublisher().getId(),
                book.getPublisher().getName(),
                book.getPublisher().getSlug()
        );
    }

    CategoryDTO categoryDTO = null;
    if (book.getCategory() != null) {
        categoryDTO = new CategoryDTO(
             book.getCategory().getId(),
                book.getCategory().getName(),
                book.getCategory().getSlug()
        );
    }

List<ImageBookDTO> imageDTOs = (book.getImages() != null && !book.getImages().isEmpty())
        ? book.getImages().stream()
            .sorted((i1, i2) -> i1.getCreatedAt().compareTo(i2.getCreatedAt()))
            .map(img -> new ImageBookDTO(
                    img.getId(),
                    img.getImage(),
                    img.getCreatedAt() != null ? img.getCreatedAt().format(formatter) : null
            ))
            .collect(Collectors.toList())
        : Collections.emptyList();

    return new BookDetailDTO(
            book.getId(),
            book.getTitle(),
            book.getPrice(),
            book.getDiscount(),
            book.getDescription(),
            book.getSlug(),
            book.getNumberOfPages(),
            book.getPublicationDate(),
            book.getWeight(),
            book.getWidth(),
            book.getLength(),
            book.getThickness(),
            book.getStock(),
            book.getStatus(),
            book.getCreatedAt() != null ? book.getCreatedAt().format(formatter) : null,
            authorDTO,
            publisherDTO,
            categoryDTO,
            imageDTOs
    );
}

// tạo sách
@Transactional
public Book createBook(Book book, List<MultipartFile> files) {
    String slug = SlugUtil.toSlug(book.getTitle());
    book.setSlug(slug);

    if (bookRepository.findByTitle(book.getTitle()).isPresent()) {
        throw new IllegalArgumentException("Book title already exists");
    }

    Book savedBook = bookRepository.save(book);
    String id = savedBook.getId();

    if (files != null && !files.isEmpty()) {
        String uploadRoot = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "books" + File.separator + id;
        File bookDir = new File(uploadRoot);
        if (!bookDir.exists()) {
            bookDir.mkdirs();
        }

        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "webp");
        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/webp");

        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                if (originalName == null) throw new IllegalArgumentException("File name is invalid");

                String extension = "";
                if (originalName.contains(".")) {
                    extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
                }

                if (!allowedExtensions.contains(extension)) {
                    throw new IllegalArgumentException("Only JPG, PNG, or WEBP images are allowed");
                }

                String contentType = file.getContentType();
                if (contentType == null || !allowedMimeTypes.contains(contentType)) {
                    throw new IllegalArgumentException("Invalid image type: " + contentType);
                }

                String filename = id + "_" + UUID.randomUUID() + "." + extension;
                File destinationFile = new File(bookDir, filename);
                file.transferTo(destinationFile);

                String relativePath = "/uploads/books/" + id + "/" + filename;

                ImageBook imageBook = ImageBook.builder()
                        .image(relativePath)
                        .book(savedBook)
                        .build();

                imageBookRepository.save(imageBook);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image: " + file.getOriginalFilename(), e);
            }
        }
    }

    return savedBook;
}

// cập nhật sách
@Transactional
public Book updateBook(String id, Book updatedBookData, List<MultipartFile> files) {
    Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Book not found"));

if (updatedBookData.getStatus() != null && updatedBookData.getStatus() == 1) {
    if (existingBook.getCategory() != null && existingBook.getCategory().getStatus() == 0) {
        throw new IllegalStateException("Cannot show book because its category is hidden");
    }
}

    existingBook.setTitle(updatedBookData.getTitle());
    existingBook.setPrice(updatedBookData.getPrice());
    existingBook.setDiscount(updatedBookData.getDiscount());
    existingBook.setDescription(updatedBookData.getDescription());
    existingBook.setPublicationDate(updatedBookData.getPublicationDate());
    existingBook.setNumberOfPages(updatedBookData.getNumberOfPages());
    existingBook.setWeight(updatedBookData.getWeight());
    existingBook.setWidth(updatedBookData.getWidth());
    existingBook.setLength(updatedBookData.getLength());
    existingBook.setThickness(updatedBookData.getThickness());
    existingBook.setStock(updatedBookData.getStock());
    existingBook.setStatus(updatedBookData.getStatus());

    if (updatedBookData.getAuthor() != null) existingBook.setAuthor(updatedBookData.getAuthor());
    if (updatedBookData.getPublisher() != null) existingBook.setPublisher(updatedBookData.getPublisher());
    if (updatedBookData.getCategory() != null) existingBook.setCategory(updatedBookData.getCategory());

    String newSlug = SlugUtil.toSlug(existingBook.getTitle());
    existingBook.setSlug(newSlug);

    Book updatedBook = bookRepository.save(existingBook);

    if (files != null && !files.isEmpty()) {
        String uploadRoot = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "books" + File.separator + id;
        File bookDir = new File(uploadRoot);
        if (!bookDir.exists()) bookDir.mkdirs();

        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "webp");
        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/webp");

        for (MultipartFile file : files) {
            try {
                String originalName = file.getOriginalFilename();
                if (originalName == null) throw new IllegalArgumentException("Invalid file name");

                String extension = "";
                if (originalName.contains(".")) {
                    extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
                }

                if (!allowedExtensions.contains(extension)) {
                    throw new IllegalArgumentException("Only JPG, PNG, or WEBP images are allowed");
                }

                String contentType = file.getContentType();
                if (contentType == null || !allowedMimeTypes.contains(contentType)) {
                    throw new IllegalArgumentException("Invalid image type: " + contentType);
                }

                String filename = id + "_" + UUID.randomUUID() + "." + extension;
                File destinationFile = new File(bookDir, filename);
                file.transferTo(destinationFile);

                String relativePath = "/uploads/books/" + id + "/" + filename;

                ImageBook imageBook = ImageBook.builder()
                        .image(relativePath)
                        .book(updatedBook)
                        .build();

                imageBookRepository.save(imageBook);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image: " + file.getOriginalFilename(), e);
            }
        }
    }

    return updatedBook;
}

// cập nhật status sách
public Book updateBookStatus(String id, Integer status) {
        return bookRepository.findById(id)
                .map(book -> {
                if (status == 1) {
                    if (book.getCategory().getStatus() == 0) {
                        throw new IllegalStateException("Cannot show book because its category is hidden");
                    }
                }
                    book.setStatus(status);
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    // xóa sách
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

// image book

// cập nhật hình của sách
@Transactional
public void updateImagesBook(List<MultipartFile> files, List<String> oldImageIds) {
    if (files == null || oldImageIds == null || files.isEmpty() || oldImageIds.isEmpty()) {
        return; 
    }

    int size = Math.min(files.size(), oldImageIds.size());

    for (int i = 0; i < size; i++) {
        String imageId = oldImageIds.get(i);
        MultipartFile file = files.get(i);

        ImageBook imageBook = imageBookRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Book image not found"));

        try {
            String path = System.getProperty("user.dir") + File.separator + imageBook.getImage();
            File oldFile = new File(path);
            if (!oldFile.exists()) {
                throw new RuntimeException("The old file does not exist: " + oldFile.getPath());
            }

            file.transferTo(oldFile);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save image: " + file.getOriginalFilename(), e);
        }
    }
}

// xóa hình của sách
 @Transactional
    public void deleteImageBook(String imageId) {
        ImageBook image = imageBookRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        File file = new File(System.getProperty("user.dir") + image.getImage());
        if (file.exists()) {
            file.delete();
        }

        imageBookRepository.delete(image);
    }

}
