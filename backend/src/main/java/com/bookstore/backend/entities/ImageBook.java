package com.bookstore.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 250)    
    private String image;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;
    
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
