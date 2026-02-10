package es.jlrn.presentation.universilabs.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicResponseDTO;

public interface ISubtopicService {
//
    Page<SubtopicResponseDTO> findAll(Pageable pageable);

    List<SubtopicResponseDTO> findByTopicId(Long topicId);

    SubtopicResponseDTO findById(Long id);

    SubtopicResponseDTO save(SubtopicRequestDTO dto);

    SubtopicResponseDTO update(Long id, SubtopicRequestDTO dto);

    void delete(Long id);
}