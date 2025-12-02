package com.bookstore.backend.service;

import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.PublisherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PublisherServiceTest {

    @Mock
    private PublisherRepository publisherRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PublisherService publisherService;

    private Publisher publisher;

    @BeforeEach
    void setup() {
        publisher = Publisher.builder()
                .id("pub1")
                .name("NXB ABC")
                .slug("nxb-abc")
                .build();
    }

    // ============================================================
    // GET ALL PUBLISHERS
    // ============================================================

    @Test
    void testGetAllPublishers_WithQuery() {
        Page<Publisher> page = new PageImpl<>(List.of(publisher));

        when(publisherRepository.findByNameContainingIgnoreCase(
                eq("abc"),
                any(Pageable.class)
        )).thenReturn(page);

        Page<Publisher> result = publisherService.getAllPublishers(1, 10, "abc");

        assertEquals(1, result.getTotalElements());
        verify(publisherRepository).findByNameContainingIgnoreCase(eq("abc"), any(Pageable.class));
    }

    @Test
    void testGetAllPublishers_NoQuery() {
        Page<Publisher> page = new PageImpl<>(List.of(publisher));

        when(publisherRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Publisher> result = publisherService.getAllPublishers(1, 10, null);

        assertEquals(1, result.getTotalElements());
        verify(publisherRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllPublishers1() {
        when(publisherRepository.findAll(any(Sort.class)))
                .thenReturn(List.of(publisher));

        List<Publisher> list = publisherService.getAllPublishers1();

        assertEquals(1, list.size());
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Test
    void testGetPublisherById() {
        when(publisherRepository.findById("pub1")).thenReturn(Optional.of(publisher));

        Optional<Publisher> result = publisherService.getPublisherById("pub1");

        assertTrue(result.isPresent());
        assertEquals("pub1", result.get().getId());
    }

    // ============================================================
    // CREATE PUBLISHER
    // ============================================================

    @Test
    void testCreatePublisher_Success() {
        Publisher newPub = Publisher.builder().name("New Pub").build();

        when(publisherRepository.findByName("New Pub")).thenReturn(Optional.empty());
        when(publisherRepository.save(any())).thenAnswer(inv -> {
            Publisher p = inv.getArgument(0);
            p.setId("pub123");
            return p;
        });

        Publisher result = publisherService.createPublisher(newPub);

        assertNotNull(result.getId());
        assertEquals("pub123", result.getId());
        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    void testCreatePublisher_NameExists() {
        when(publisherRepository.findByName("NXB ABC")).thenReturn(Optional.of(publisher));

        Publisher newPub = Publisher.builder().name("NXB ABC").build();

        assertThrows(IllegalArgumentException.class,
                () -> publisherService.createPublisher(newPub));
    }

    // ============================================================
    // UPDATE PUBLISHER
    // ============================================================

    @Test
    void testUpdatePublisher_Success() {
        Publisher updateData = Publisher.builder().name("New Name").build();

        when(publisherRepository.findById("pub1")).thenReturn(Optional.of(publisher));
        when(publisherRepository.findByName("New Name")).thenReturn(Optional.empty());
        when(publisherRepository.save(any())).thenReturn(publisher);

        Publisher result = publisherService.updatePublisher("pub1", updateData);

        assertEquals("New Name", result.getName());
        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    void testUpdatePublisher_NameExists() {
        Publisher conflict = Publisher.builder().id("pub999").name("New Name").build();
        Publisher updateData = Publisher.builder().name("New Name").build();

        when(publisherRepository.findById("pub1")).thenReturn(Optional.of(publisher));
        when(publisherRepository.findByName("New Name")).thenReturn(Optional.of(conflict));

        assertThrows(IllegalArgumentException.class,
                () -> publisherService.updatePublisher("pub1", updateData));
    }

    @Test
    void testUpdatePublisher_NotFound() {
        when(publisherRepository.findById("pub1")).thenReturn(Optional.empty());

        Publisher updateData = Publisher.builder().name("New Name").build();

        assertThrows(EntityNotFoundException.class,
                () -> publisherService.updatePublisher("pub1", updateData));
    }

    // ============================================================
    // DELETE PUBLISHER
    // ============================================================

    @Test
    void testDeletePublisher_Success() {
        when(publisherRepository.findById("pub1")).thenReturn(Optional.of(publisher));
        when(bookRepository.existsByPublisher(publisher)).thenReturn(false);

        assertDoesNotThrow(() -> publisherService.deletePublisher("pub1"));
        verify(publisherRepository).deleteById("pub1");
    }

    @Test
    void testDeletePublisher_NotFound() {
        when(publisherRepository.findById("pub1")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> publisherService.deletePublisher("pub1"));
    }

    @Test
    void testDeletePublisher_HasBooks() {
        when(publisherRepository.findById("pub1")).thenReturn(Optional.of(publisher));
        when(bookRepository.existsByPublisher(publisher)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> publisherService.deletePublisher("pub1"));
    }
}
