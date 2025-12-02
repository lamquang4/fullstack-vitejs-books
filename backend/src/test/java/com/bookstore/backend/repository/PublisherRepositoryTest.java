package com.bookstore.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.backend.entities.Publisher;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PublisherRepositoryTest {

  @Autowired private PublisherRepository publisherRepository;

  private Publisher publisher1;
  private Publisher publisher2;

  @BeforeEach
  void setup() {
    publisher1 =
        publisherRepository.save(Publisher.builder().name("NXB Trẻ").slug("nxb-tre").build());

    publisher2 =
        publisherRepository.save(Publisher.builder().name("Kim Đồng").slug("kim-dong").build());
  }

  // =============================================================
  // findByName
  // =============================================================
  @Test
  void findByName_shouldReturnPublisher() {
    Optional<Publisher> result = publisherRepository.findByName("NXB Trẻ");

    assertThat(result).isPresent();
    assertThat(result.get().getSlug()).isEqualTo("nxb-tre");
  }

  @Test
  void findByName_shouldReturnEmpty_whenNotFound() {
    Optional<Publisher> result = publisherRepository.findByName("Không tồn tại");

    assertThat(result).isNotPresent();
  }

  // =============================================================
  // findByNameContainingIgnoreCase
  // =============================================================
  @Test
  void findByNameContainingIgnoreCase_shouldReturnMatchingPublishers() {
    var result = publisherRepository.findByNameContainingIgnoreCase("nxb", PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("NXB Trẻ");
  }

  @Test
  void findByNameContainingIgnoreCase_shouldReturnAllMatching() {
    var result = publisherRepository.findByNameContainingIgnoreCase("đ", PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent().get(0).getName()).isEqualTo("Kim Đồng");
  }

  @Test
  void findByNameContainingIgnoreCase_shouldReturnEmpty_whenNoMatch() {
    var result = publisherRepository.findByNameContainingIgnoreCase("abc", PageRequest.of(0, 10));

    assertThat(result.getTotalElements()).isZero();
  }
}
