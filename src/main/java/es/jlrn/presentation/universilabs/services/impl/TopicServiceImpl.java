package es.jlrn.presentation.universilabs.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.exceptions.exception.ResourceConflictException;
import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.CollectionEntity;
import es.jlrn.persistence.models.universilabs.models.Topic;
import es.jlrn.persistence.models.universilabs.repositories.CollectionRepository;
import es.jlrn.persistence.models.universilabs.repositories.TopicRepository;
import es.jlrn.presentation.universilabs.dtos.topics.TopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.topics.TopicResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ITopicMapper;
import es.jlrn.presentation.universilabs.services.interfaces.ITopicService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements ITopicService {
//
    private final TopicRepository topicRepository;
    private final CollectionRepository collectionRepository;
    private final ITopicMapper topicMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TopicResponseDTO> findAll(Pageable pageable) {
        // 1. Obtenemos la página de entidades
        Page<Topic> topicsPage = topicRepository.findAll(pageable);
        
        // 2. Convertimos la página de entidades a página de DTOs usando tu mapper
        return topicsPage.map(topicMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicResponseDTO> findByCollectionId(Long collectionId) {
        if (!collectionRepository.existsById(collectionId)) {
            throw new ResourceNotFoundException("No se pueden listar temas: Colección no encontrada con ID: " + collectionId);
        }
        return topicRepository.findByCollectionId(collectionId).stream()
                .map(topicMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TopicResponseDTO findById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        return topicMapper.toResponseDTO(topic);
    }

    @Override
    @Transactional
    public TopicResponseDTO save(TopicRequestDTO dto) {
        // 1. Validar que la colección existe
        CollectionEntity collection = collectionRepository.findById(dto.collectionId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el tema: La colección no existe."));

        // 2. Validar que no exista otro tema con el mismo título en esta colección
        topicRepository.findByTitleAndCollectionId(dto.title(), dto.collectionId())
                .ifPresent(t -> {
                    throw new ResourceConflictException(
                        String.format("Ya existe un tema llamado '%s' en esta colección.", dto.title()));
                });

        // 3. Crear entidad y asignar relación
        Topic topic = topicMapper.toEntity(dto);
        topic.setCollection(collection);

        return topicMapper.toResponseDTO(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public TopicResponseDTO update(Long id, TopicRequestDTO dto) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado para actualizar."));

        boolean titleChanged = !topic.getTitle().equalsIgnoreCase(dto.title());
        boolean collectionChanged = !topic.getCollection().getId().equals(dto.collectionId());

        if (titleChanged || collectionChanged) {
            topicRepository.findByTitleAndCollectionId(dto.title(), dto.collectionId())
                    .ifPresent(t -> {
                        throw new ResourceConflictException("Conflicto: El título ya está en uso en la colección destino.");
                    });

            if (collectionChanged) {
                CollectionEntity newCollection = collectionRepository.findById(dto.collectionId())
                        .orElseThrow(() -> new ResourceNotFoundException("La colección destino no existe."));
                topic.setCollection(newCollection);
            }
        }

        topic.setTitle(dto.title());
        return topicMapper.toResponseDTO(topicRepository.save(topic));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Tema no encontrado.");
        }
        topicRepository.deleteById(id);
    }
}