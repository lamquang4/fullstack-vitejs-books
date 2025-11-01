package com.bookstore.backend.service;
import com.bookstore.backend.dto.AuthorDTO;
import com.bookstore.backend.dto.BookDTO;
import com.bookstore.backend.dto.BookDetailDTO;
import com.bookstore.backend.dto.CategoryDTO;
import com.bookstore.backend.dto.ImageBookDTO;
import com.bookstore.backend.dto.PublisherDTO;
import com.bookstore.backend.entities.Book;
import com.bookstore.backend.entities.Category;
import com.bookstore.backend.entities.ImageBook;
import com.bookstore.backend.repository.CategoryRepository;
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
import java.util.Set;
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
    private final CategoryRepository categoryRepository;
    public BookService(BookRepository bookRepository, ImageBookRepository imageBookRepository, OrderDetailRepository orderDetailRepository, CartItemRepository cartItemRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.imageBookRepository = imageBookRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.cartItemRepository = cartItemRepository;
        this.categoryRepository = categoryRepository;
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
    public Page<BookDTO> getAllActiveBooks(int page, String q, String sort, Integer min, Integer max) {
        int limit = 12;
        int status = 1;
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

        Page<Book> bookPage;

        if (q != null && !q.isEmpty()) {
            bookPage = bookRepository.searchByTitleAuthorPublisherCategoryAndPrice(q, status, min, max, pageable);
        } else {
            if (sort == null || sort.isEmpty()) {
                bookPage = bookRepository.findByStatusAndPriceRange(status, min, max, pageable);
            } else {
                switch (sort) {
                    case "price-asc":
                        bookPage = bookRepository.findByStatusAndPriceRangeOrderByEffectivePriceAsc(status, min, max, pageable);
                        break;
                    case "price-desc":
                        bookPage = bookRepository.findByStatusAndPriceRangeOrderByEffectivePriceDesc(status, min, max, pageable);
                        break;
                    case "bestseller":
                        bookPage = bookRepository.findByStatusAndPriceRangeOrderByTotalSold(status, min, max, pageable);
                        break;
                    default:
                        bookPage = bookRepository.findByStatusAndPriceRange(status, min, max, pageable);
                }
            }
        }

        return bookPage.map(this::convertToDTO);
    }


    // lây sách có status = 1 và giảm giá 
   public Page<BookDTO> getDiscountedActiveBooks(int page, String q, String sort, Integer min, Integer max) {
    int limit = 12;
    int status = 1;
     Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

    Page<Book> bookPage;

    if (q != null && !q.isEmpty()) {
        bookPage = bookRepository.searchDiscountedActiveCategoryAndPrice(q, status, min, max, pageable);
    } else {
        if (sort == null || sort.isEmpty()) {
            bookPage = bookRepository.findDiscountedAndPriceRange(status, min, max, pageable);
        } else {
            switch (sort) {
                case "price-asc":
                    bookPage = bookRepository.findDiscountedAndPriceRangeOrderByEffectivePriceAsc(status, min, max, pageable);
                    break;
                case "price-desc":
                    bookPage = bookRepository.findDiscountedAndPriceRangeOrderByEffectivePriceDesc(status, min, max, pageable);
                    break;
                case "bestseller":
                    bookPage = bookRepository.findDiscountedAndPriceRangeOrderByTotalSold(status, min, max, pageable);
                    break;
                default:
                    bookPage = bookRepository.findDiscountedAndPriceRange(status, min, max, pageable);
            }
        }
    }

    return bookPage.map(this::convertToDTO);
}

    // lấy sách có status = 1 theo category
 public Page<BookDTO> getActiveBooksByCategory(String slug, int page, String q, String sort, Integer min, Integer max) {
    int limit = 12;
    int status = 1;
      Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());

    Page<Book> bookPage;

    if (q != null && !q.isEmpty()) {
        bookPage = bookRepository.searchByCategoryAndTitleAuthorPublisherCategoryAndPrice(slug, q, status, min, max, pageable);
    } else {
        if (sort == null || sort.isEmpty()) {
            bookPage = bookRepository.findByCategorySlugAndStatusAndPriceRange(slug, status, min, max, pageable);
        } else {
            switch (sort) {
                case "price-asc":
                    bookPage = bookRepository.findByCategorySlugAndStatusAndPriceRangeOrderByEffectivePriceAsc(slug, status, min, max, pageable);
                    break;
                case "price-desc":
                    bookPage = bookRepository.findByCategorySlugAndStatusAndPriceRangeOrderByEffectivePriceDesc(slug, status, min, max, pageable);
                    break;
                case "bestseller":
                    bookPage = bookRepository.findByCategorySlugAndStatusAndPriceRangeOrderByTotalSold(slug, status, min, max, pageable);
                    break;
                default:
                    bookPage = bookRepository.findByCategorySlugAndStatusAndPriceRange(slug, status, min, max, pageable);
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
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sách"));

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
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sách"));

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
        throw new IllegalArgumentException("Tiêu đề của sách đã tồn tại");
    }

    if (book.getPrice() == null || book.getPrice() <= 0) {
        throw new IllegalArgumentException("Giá gốc phải lớn hơn 0");
    }

    if (book.getDiscount() != null) {
        if (book.getDiscount() < 0) {
            throw new IllegalArgumentException("Giảm giá phải lớn hơn hoặc bằng 0");
        }
        if (book.getDiscount() > book.getPrice()) {
            throw new IllegalArgumentException("Giảm giá không được lớn hơn giá gốc");
        }
    }

    if (book.getStock() == null || book.getStock() < 0) {
        throw new IllegalArgumentException("Số lượng hiện có phải lớn hơn hoặc bằng 0");
    }

    if (book.getNumberOfPages() == null || book.getNumberOfPages() <= 0) {
        throw new IllegalArgumentException("Số trang phải lớn hơn 0");
    }

    if (book.getWeight() == null || book.getWeight() <= 0) {
        throw new IllegalArgumentException("Khối lượng phải lớn hơn 0");
    }

    if (book.getWidth() == null || book.getWidth() <= 0) {
        throw new IllegalArgumentException("Chiều rộng phải lớn hơn 0");
    }

    if (book.getLength() == null || book.getLength() <= 0) {
        throw new IllegalArgumentException("Chiều dài phải lớn hơn 0");
    }

    if (book.getThickness() == null || book.getThickness() <= 0) {
        throw new IllegalArgumentException("Độ dày phải lớn hơn 0");
    }

    if (book.getCategory() != null && book.getCategory().getId() != null) {
        String categoryId = book.getCategory().getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục"));
        book.setCategory(category);

        if (Integer.valueOf(0).equals(category.getStatus())) {
            book.setStatus(0); 
        }   
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
                if (originalName == null) throw new IllegalArgumentException("Tên file hình không hợp lệ");

                String extension = "";
                if (originalName.contains(".")) {
                    extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
                }

                if (!allowedExtensions.contains(extension)) {
                    throw new IllegalArgumentException("Chỉ cho phép hình JPG, PNG hoặc WEBP");
                }

                String contentType = file.getContentType();
                if (contentType == null || !allowedMimeTypes.contains(contentType)) {
                    throw new IllegalArgumentException("Chỉ cho phép hình JPG, PNG hoặc WEBP " + contentType);
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
             throw new RuntimeException("Lưu hình "  + file.getOriginalFilename() +  " thất bại: ", e);
            }
        }
    }

    return savedBook;
}

// cập nhật sách
@Transactional
public Book updateBook(String id, Book updatedBookData, List<MultipartFile> files) {
    Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Sách không tìm thấy"));

    if (updatedBookData.getPrice() <= 0) {
        throw new IllegalArgumentException("Giá gốc phải lớn hơn 0");
    }

    if (updatedBookData.getDiscount() < 0) {
        throw new IllegalArgumentException("Số tiền giảm phải lớn hơn hoặc bằng 0");
    }

    if (updatedBookData.getDiscount() > updatedBookData.getPrice()) {
        throw new IllegalArgumentException("Số tiền giảm không được lớn hơn giá gốc");
    }

    if (updatedBookData.getStock() < 0) {
        throw new IllegalArgumentException("Số lượng hiện có phải lớn hơn hoặc bằng 0");
    }

    if (updatedBookData.getNumberOfPages() <= 0) {
        throw new IllegalArgumentException("Số trang phải lớn hơn 0");
    }

    if (updatedBookData.getWeight() <= 0) {
        throw new IllegalArgumentException("Khối lượng phải lớn hơn 0");
    }

    if (updatedBookData.getWidth() <= 0) {
        throw new IllegalArgumentException("Chiều rộng phải lớn hơn 0");
    }

    if (updatedBookData.getLength() <= 0) {
        throw new IllegalArgumentException("Chiều dài phải lớn hơn 0");
    }

    if (updatedBookData.getThickness() <= 0) {
        throw new IllegalArgumentException("Độ dày phải lớn hơn 0");
    }

    if (updatedBookData.getStatus() != null && updatedBookData.getStatus() == 1) {
        if (existingBook.getCategory() != null && existingBook.getCategory().getStatus() == 0) {
            throw new IllegalStateException("Không thể hiện sách vì danh mục của sách đang bị ẩn");
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
                if (originalName == null) throw new IllegalArgumentException("Tên file hình không hợp lệ");

                String extension = "";
                if (originalName.contains(".")) {
                    extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
                }

                if (!allowedExtensions.contains(extension)) {
                    throw new IllegalArgumentException("Chỉ cho phép hình JPG, PNG hoặc WEBP");
                }

                String contentType = file.getContentType();
                if (contentType == null || !allowedMimeTypes.contains(contentType)) {
                    throw new IllegalArgumentException("Chỉ cho phép hình JPG, PNG hoặc WEBP " + contentType);
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
             throw new RuntimeException("Lưu hình "  + file.getOriginalFilename() +  " thất bại: ", e);
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
                        throw new IllegalStateException("Không thể hiện sách vì danh mục của sách đang bị ẩn");
                    }
                }
                    book.setStatus(status);
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new EntityNotFoundException("Sách không tìm thấy"));
    }

    // xóa sách
public void deleteBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sách không tìm thấy"));

   if (orderDetailRepository.existsByBook(book)) {
        throw new IllegalStateException("Sách này không thể bị xóa vì đã được mua hàng");
    }

    if (cartItemRepository.existsByBook(book)) {
        throw new IllegalStateException("Sách này không thể bị xóa vì đang có trong giỏ hàng của người dùng");
    }

       List<ImageBook> images = imageBookRepository.findByBook(book);
    imageBookRepository.deleteAll(images);

    File bookDir = new File(UPLOAD_DIR + book.getId());
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

    Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "webp");
    Set<String> allowedMimeTypes = Set.of("image/jpeg", "image/png", "image/webp");

    int size = Math.min(files.size(), oldImageIds.size());

    for (int i = 0; i < size; i++) {
        String imageId = oldImageIds.get(i);
        MultipartFile file = files.get(i);

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("Tên file hình không hợp lệ");
        }

        String extension = "";
        if (originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Chỉ cho phép hình JPG, PNG hoặc WEBP");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedMimeTypes.contains(contentType)) {
            throw new IllegalArgumentException("Chỉ cho phép hình JPG, PNG hoặc WEBP " + contentType);
        }

        ImageBook imageBook = imageBookRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Hình không tìm thấy"));

        try {
            String path = System.getProperty("user.dir") + File.separator + imageBook.getImage();
            File oldFile = new File(path);
            if (!oldFile.exists()) {
                throw new RuntimeException("Lỗi đường dẫn " + oldFile.getPath());
            }

            file.transferTo(oldFile);

        } catch (IOException e) {
            throw new RuntimeException("Lưu hình "  + file.getOriginalFilename() +  " thất bại: ", e);
        }
    }
}

// xóa hình của sách
 @Transactional
    public void deleteImageBook(String imageId) {
        ImageBook image = imageBookRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Hình không tìm thấy"));

        File file = new File(System.getProperty("user.dir") + image.getImage());
        if (file.exists()) {
            file.delete();
        }

        imageBookRepository.delete(image);
    }

}
