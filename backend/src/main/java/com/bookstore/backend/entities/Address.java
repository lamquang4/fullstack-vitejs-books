package com.bookstore.backend.entities;
import java.time.LocalDateTime;
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
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
@GeneratedValue(strategy = GenerationType.UUID)
private String id;

    @Column(nullable = false, length = 50)
    private String fullname;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, length = 70)
    private String speaddress; 

    @Column(nullable = false, length = 70)
    private String ward;

    @Column(nullable = false, length = 70)
    private String city;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
  private User user;

    @Builder.Default
@Column(nullable = false)
private LocalDateTime createdAt = LocalDateTime.now();
}
