package com.bookstore.backend.controller;
import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.service.PublisherService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/publisher")
@CrossOrigin(origins = "*")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPublishers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String q
    ) {
        Page<Publisher> publisherPage = publisherService.getAllPublishers(page, limit, q);

        return ResponseEntity.ok(Map.of(
            "publishers", publisherPage.getContent(),
            "totalPages", publisherPage.getTotalPages(),
            "total", publisherPage.getTotalElements()
        ));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Publisher>> getAllPublishers1() {
        List<Publisher> publishers = publisherService.getAllPublishers1();
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publisher> getPublisherById(@PathVariable String id) {
        return publisherService.getPublisherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Publisher createPublisher(@RequestBody Publisher publisher) {
        return publisherService.createPublisher(publisher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Publisher> updatePublisher(@PathVariable String id, @RequestBody Publisher publisher) {
        Publisher updated = publisherService.updatePublisher(id, publisher);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable String id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}
