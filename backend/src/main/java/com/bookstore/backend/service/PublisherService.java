package com.bookstore.backend.service;
import com.bookstore.backend.entities.Publisher;
import com.bookstore.backend.repository.BookRepository;
import com.bookstore.backend.repository.PublisherRepository;
import com.bookstore.backend.utils.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;
        private final BookRepository bookRepository;

    public PublisherService(PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.publisherRepository = publisherRepository;
               this.bookRepository = bookRepository;
    }

public Page<Publisher> getPublishers(int page, int limit, String q) {
    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
    if (q != null && !q.isEmpty()) {
        return publisherRepository.findByNameContainingIgnoreCase(q, pageable);
    }
    return publisherRepository.findAll(pageable);
}

    public Optional<Publisher> getPublisherById(String id) {
        return publisherRepository.findById(id);
    }

    public Publisher createPublisher(Publisher publisher) {
         if (publisherRepository.findByName(publisher.getName()).isPresent()) {
        throw new IllegalArgumentException("Publisher name already exists");
    }
        publisher.setSlug(SlugUtil.toSlug(publisher.getName()));
        return publisherRepository.save(publisher);
    }

    public Publisher updatePublisher(String id, Publisher publisher) {
        return publisherRepository.findById(id)
                .map(existingPublisher -> {
                    publisherRepository.findByName(publisher.getName())
                    .filter(a -> !a.getId().equals(id))
                    .ifPresent(a -> { throw new IllegalArgumentException("Publisher name already exists"); });

                    publisher.setSlug(SlugUtil.toSlug(publisher.getName()));
                    existingPublisher.setName(publisher.getName());
                    existingPublisher.setSlug(publisher.getSlug());
                    return publisherRepository.save(existingPublisher);
                })
                .orElse(null);
    }

    public void deletePublisher(String id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found publisher"));

        // không cho xóa khi có id publisher ở book
        if (bookRepository.existsByPublisher(publisher)) {
            throw new IllegalStateException("This publisher cannot be deleted as they have books associated with them");
        }
        publisherRepository.deleteById(id);
    }
}
