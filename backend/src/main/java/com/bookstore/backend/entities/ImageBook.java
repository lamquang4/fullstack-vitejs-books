package com.bookstore.backend.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagebook")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageBook {
        @Id
@GeneratedValue(strategy = GenerationType.UUID)
private String id;

    @Column(nullable = false, unique = true, length = 50)    
    private String image;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;
}
