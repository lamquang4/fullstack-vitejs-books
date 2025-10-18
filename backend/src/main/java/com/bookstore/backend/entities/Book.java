package com.bookstore.backend.entities;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, unique = true, length = 50)    
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
private List<ImageBook> images; // khi xóa book thì imagebook cũng bị xóa theo

}
