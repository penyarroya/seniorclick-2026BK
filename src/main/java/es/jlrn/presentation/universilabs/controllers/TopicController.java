package es.jlrn.presentation.universilabs.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.jlrn.presentation.universilabs.dtos.topics.TopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.topics.TopicResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.ITopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
//
    private final ITopicService topicService;

    @GetMapping
    public ResponseEntity<Page<TopicResponseDTO>> getAll(Pageable pageable) {
        // Este es el método que usa el MaintenanceLayout de Angular
        return ResponseEntity.ok(topicService.findAll(pageable));
    }

    @GetMapping("/collection/{collectionId}")
    public ResponseEntity<List<TopicResponseDTO>> getTopicsByCollection(@PathVariable Long collectionId) {
        return ResponseEntity.ok(topicService.findByCollectionId(collectionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TopicResponseDTO> createTopic(@Valid @RequestBody TopicRequestDTO dto) {
        return new ResponseEntity<>(topicService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseDTO> updateTopic(@PathVariable Long id, @Valid @RequestBody TopicRequestDTO dto) {
        return ResponseEntity.ok(topicService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}