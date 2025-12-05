package com.bookstore.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.backend.entities.*;
import com.bookstore.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BookIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private AuthorRepository authorRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private ImageBookRepository imageBookRepository;
  @Autowired private OrderRepository orderRepository;
  @Autowired private OrderDetailRepository orderDetailRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CartItemRepository cartItemRepository;

  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  private Author author;
  private Category category;
  private Publisher publisher;

  private final File uploadsRoot =
      new File(
          System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "books");

  @BeforeEach
  void beforeEach() throws Exception {
    orderDetailRepository.deleteAll();
    orderRepository.deleteAll();
    cartItemRepository.deleteAll();
    imageBookRepository.deleteAll();
    bookRepository.deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
    publisherRepository.deleteAll();
    userRepository.deleteAll();

    if (uploadsRoot.exists()) {
      deleteRecursively(uploadsRoot);
    }

    author =
        Author.builder()
            .fullname("Integration Author")
            .slug("integration-author")
            .createdAt(LocalDateTime.now())
            .build();
    author = authorRepository.save(author);

    category =
        Category.builder()
            .name("Integration Cat")
            .slug("integration-cat")
            .status(1)
            .createdAt(LocalDateTime.now())
            .build();
    category = categoryRepository.save(category);

    publisher =
        Publisher.builder()
            .name("Integration Pub")
            .slug("integration-pub")
            .createdAt(LocalDateTime.now())
            .build();
    publisher = publisherRepository.save(publisher);
  }

  @AfterEach
  void afterEach() throws Exception {
    orderDetailRepository.deleteAll();
    orderRepository.deleteAll();
    cartItemRepository.deleteAll();
    imageBookRepository.deleteAll();
    bookRepository.deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
    publisherRepository.deleteAll();
    userRepository.deleteAll();

    if (uploadsRoot.exists()) {
      deleteRecursively(uploadsRoot);
    }
  }

  private void deleteRecursively(File f) throws Exception {
    if (f.isDirectory()) {
      for (File c : f.listFiles()) {
        deleteRecursively(c);
      }
    }
    Files.deleteIfExists(f.toPath());
  }

  private Book buildBookEntityTemplate() {
    Book b = new Book();
    b.setTitle("Integration Test Book");
    b.setSlug("integration-test-book");
    b.setPrice(150.0);
    b.setDiscount(10.0);
    b.setDescription("Integration test description");
    b.setPublicationDate(LocalDate.of(2025, 1, 1));
    b.setNumberOfPages(120);
    b.setWeight(0.5);
    b.setWidth(10.0);
    b.setLength(20.0);
    b.setThickness(1.0);
    b.setStock(50);
    b.setStatus(1);
   
    Category c = new Category();
    c.setId(category.getId());
    b.setCategory(c);

    Author a = new Author();
    a.setId(author.getId());
    b.setAuthor(a);

    Publisher p = new Publisher();
    p.setId(publisher.getId());
    b.setPublisher(p);

    return b;
  }

  // CREATE - POST multipart
  @Test
  void createBook_withMultipart_shouldReturnOk_and_saveImages() throws Exception {
    Book book = buildBookEntityTemplate();

    String jsonBook = mapper.writeValueAsString(book);
    MockMultipartFile bookPart =
        new MockMultipartFile("book", "", "application/json", jsonBook.getBytes());
    MockMultipartFile imagePart =
        new MockMultipartFile("files", "cover.jpg", "image/jpeg", "fake-image-bytes".getBytes());

    mockMvc
        .perform(multipart("/api/book").file(bookPart).file(imagePart).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.slug").exists());

    List<Book> books = bookRepository.findAll();
    assertThat(books).hasSize(1);
    Book saved = books.get(0);

    File bookDir =
        new File(
            System.getProperty("user.dir")
                + File.separator
                + "uploads"
                + File.separator
                + "books"
                + File.separator
                + saved.getId());
    assertThat(bookDir.exists()).isTrue();

    List<ImageBook> imgs = imageBookRepository.findByBook(saved);
    assertThat(imgs).isNotEmpty();

    assertThat(imgs.get(0).getImage()).contains("/uploads/books/" + saved.getId() + "/");
    File actualImg = new File(System.getProperty("user.dir") + imgs.get(0).getImage());
    assertThat(actualImg.exists()).isTrue();
  }

  // GET paging
  @Test
  void getAllBooks_shouldReturnPagingResult() throws Exception {
    Book b = buildBookEntityTemplate();
    b.setSlug("integration-book");
    Book saved = bookRepository.save(b);

    mockMvc
        .perform(get("/api/book").param("page", "1").param("limit", "12"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.books[0].title").value("Integration Test Book"))
        .andExpect(jsonPath("$.total").value(1));
  }

  // GET by slug & id
  @Test
  void getBookBySlugAndById_shouldReturnDetail() throws Exception {
    Book b = buildBookEntityTemplate();
    b.setSlug("slug-book");
    Book saved = bookRepository.save(b);

    mockMvc
        .perform(get("/api/book/slug/{slug}", "slug-book"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Integration Test Book"));

    mockMvc
        .perform(get("/api/book/{id}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Integration Test Book"));
  }

  // DELETE book (success)
  @Test
  void deleteBook_successAndRemovesFiles() throws Exception {
    Book b = buildBookEntityTemplate();
    Book saved = bookRepository.save(b);

    File bookDir =
        new File(
            System.getProperty("user.dir")
                + File.separator
                + "uploads"
                + File.separator
                + "books"
                + File.separator
                + saved.getId());
    bookDir.mkdirs();
    File img = new File(bookDir, "f.jpg");
    Files.write(img.toPath(), "abc".getBytes());
    ImageBook imageBook =
        ImageBook.builder()
            .image("/uploads/books/" + saved.getId() + "/f.jpg")
            .book(saved)
            .createdAt(LocalDateTime.now())
            .build();
    imageBookRepository.save(imageBook);

    mockMvc
        .perform(delete("/api/book/{id}", saved.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    assertThat(bookRepository.findById(saved.getId())).isEmpty();
    assertThat(img.exists()).isFalse();
  }

  // --------------------
  // DELETE book (in order) -> should return 409/BadRequest depending on your handler
  // --------------------
  @Test
  void deleteBook_inOrder_shouldFail() throws Exception {
    Book b = buildBookEntityTemplate();
    Book saved = bookRepository.save(b);

    // create user + order + orderDetail
    User user =
        User.builder().email("u@t.com").fullname("U").password("p").role(3).status(1).build();
    user = userRepository.save(user);

    Order order =
        Order.builder()
            .orderCode("ODX")
            .fullname("A")
            .phone("0")
            .speaddress("a")
            .city("c")
            .ward("w")
            .paymethod("COD")
            .status(3)
            .total(100.0)
            .user(user)
            .build();
    order = orderRepository.save(order);

    OrderDetail od =
        OrderDetail.builder()
            .book(saved)
            .order(order)
            .price(100.0)
            .discount(0.0)
            .quantity(1)
            .build();
    orderDetailRepository.save(od);

    mockMvc
        .perform(delete("/api/book/{id}", saved.getId()).with(csrf()))
        .andExpect(status().isConflict()); // project maps IllegalStateException -> 409
  }

  // --------------------
  // updateImagesBook (PUT /api/book/image) success
  // --------------------
  @Test
  void updateImagesBook_success() throws Exception {
    // create book and image records
    Book b = buildBookEntityTemplate();
    Book saved = bookRepository.save(b);

    File bookDir =
        new File(
            System.getProperty("user.dir")
                + File.separator
                + "uploads"
                + File.separator
                + "books"
                + File.separator
                + saved.getId());
    bookDir.mkdirs();
    File imgFile = new File(bookDir, "img-old.jpg");
    Files.write(imgFile.toPath(), "old".getBytes());

    ImageBook img =
        ImageBook.builder()
            .image("/uploads/books/" + saved.getId() + "/img-old.jpg")
            .book(saved)
            .createdAt(LocalDateTime.now())
            .build();
    img = imageBookRepository.save(img);

    MockMultipartFile newImage =
        new MockMultipartFile("files", "img-old.jpg", "image/jpeg", "newcontent".getBytes());

    mockMvc
        .perform(
            multipart("/api/book/image")
                .file(newImage)
                .param("oldImageIds", img.getId())
                .with(csrf())
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    }))
        .andExpect(status().isNoContent());

    // ensure file content overwritten
    byte[] bytes = Files.readAllBytes(imgFile.toPath());
    assertThat(new String(bytes)).isEqualTo("newcontent");
  }

  // --------------------
  // deleteImageBook
  // --------------------
  @Test
  void deleteImageBook_success() throws Exception {
    Book b = buildBookEntityTemplate();
    Book saved = bookRepository.save(b);

    File bookDir =
        new File(
            System.getProperty("user.dir")
                + File.separator
                + "uploads"
                + File.separator
                + "books"
                + File.separator
                + saved.getId());
    bookDir.mkdirs();
    File imgFile = new File(bookDir, "img-del.jpg");
    Files.write(imgFile.toPath(), "x".getBytes());

    ImageBook img =
        ImageBook.builder()
            .image("/uploads/books/" + saved.getId() + "/img-del.jpg")
            .book(saved)
            .createdAt(LocalDateTime.now())
            .build();
    img = imageBookRepository.save(img);

    mockMvc
        .perform(delete("/api/book/image/{imageId}", img.getId()).with(csrf()))
        .andExpect(status().isNoContent());

    assertThat(imageBookRepository.findById(img.getId())).isEmpty();
    assertThat(imgFile.exists()).isFalse();
  }
}
