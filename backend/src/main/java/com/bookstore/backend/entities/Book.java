package com.bookstore.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 100)    
    private String title;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column( nullable = false)
    private String publicationDate;

    @Column( nullable = false)
    private Integer numberOfPages;

    @Column( nullable = false)
    private Double weight;
    @Column( nullable = false)
    private Double width;
    @Column( nullable = false)
    private Double length;
    @Column( nullable = false)
    private Double thickness;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne()
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @ManyToOne()
    @JoinColumn(name = "authorId", nullable = false)
    private Author author;

    @ManyToOne()
    @JoinColumn(name = "publisherId", nullable = false)
    private Publisher publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageBook> images;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
