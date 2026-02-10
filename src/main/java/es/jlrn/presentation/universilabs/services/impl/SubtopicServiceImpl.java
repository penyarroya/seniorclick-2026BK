package es.jlrn.presentation.universilabs.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.jlrn.exceptions.exception.ResourceConflictException;
import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.Subtopic;
import es.jlrn.persistence.models.universilabs.models.Topic;
import es.jlrn.persistence.models.universilabs.repositories.SubtopicRepository;
import es.jlrn.persistence.models.universilabs.repositories.TopicRepository;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ISubtopicMapper;
import es.jlrn.presentation.universilabs.services.interfaces.ISubtopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubtopicServiceImpl implements ISubtopicService {
//
    private final SubtopicRepository subtopicRepository;
    private final TopicRepository topicRepository;
    private final ISubtopicMapper subtopicMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<SubtopicResponseDTO> findAll(Pageable pageable) {
        // subtopicRepository.findAll(pageable) ya existe por defecto en JpaRepository
        return subtopicRepository.findAll(pageable)
                .map(subtopicMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubtopicResponseDTO> findByTopicId(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new ResourceNotFoundException("No se pueden listar subtemas: Tema no encontrado con ID: " + topicId);
        }
        return subtopicRepository.findByTopicId(topicId).stream()
                .map(subtopicMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubtopicResponseDTO findById(Long id) {
        Subtopic subtopic = subtopicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subtema no encontrado con ID: " + id));
        return subtopicMapper.toResponseDTO(subtopic);
    }

    @Override
    @Transactional
    public SubtopicResponseDTO save(SubtopicRequestDTO dto) {
        // 1. Validar que el Tema (padre) existe
        Topic topic = topicRepository.findById(dto.topicId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el subtema: El tema padre no existe."));

        // 2. Validar que no exista otro subtema con el mismo título en este tema
        subtopicRepository.findByTitleAndTopicId(dto.title(), dto.topicId())
                .ifPresent(s -> {
                    throw new ResourceConflictException(
                        String.format("Ya existe un subtema llamado '%s' en este tema.", dto.title()));
                });

        // 3. Mapear y asignar relación
        Subtopic subtopic = subtopicMapper.toEntity(dto);
        subtopic.setTopic(topic);

        return subtopicMapper.toResponseDTO(subtopicRepository.save(subtopic));
    }

    @Override
    @Transactional
    public SubtopicResponseDTO update(Long id, SubtopicRequestDTO dto) {
        Subtopic subtopic = subtopicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subtema no encontrado para actualizar."));

        boolean titleChanged = !subtopic.getTitle().equalsIgnoreCase(dto.title());
        boolean topicChanged = !subtopic.getTopic().getId().equals(dto.topicId());

        if (titleChanged || topicChanged) {
            subtopicRepository.findByTitleAndTopicId(dto.title(), dto.topicId())
                    .ifPresent(s -> {
                        throw new ResourceConflictException("Conflicto: El título del subtema ya está en uso en el tema destino.");
                    });

            if (topicChanged) {
                Topic newTopic = topicRepository.findById(dto.topicId())
                        .orElseThrow(() -> new ResourceNotFoundException("El tema destino no existe."));
                subtopic.setTopic(newTopic);
            }
        }

        subtopic.setTitle(dto.title());
        return subtopicMapper.toResponseDTO(subtopicRepository.save(subtopic));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!subtopicRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Subtema no encontrado.");
        }
        subtopicRepository.deleteById(id);
    }
}