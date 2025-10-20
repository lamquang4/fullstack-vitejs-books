package com.bookstore.backend.entities;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

     @Column(nullable = false, length = 50)
    private String fullname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer role;

    @Column(nullable = false)
    private Integer status;

    @Builder.Default
@Column(nullable = false)
private LocalDateTime createdAt = LocalDateTime.now();
}
