package es.jlrn.presentation.universilabs.dtos.topics;

import lombok.*;
import java.util.List;

import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicNodeDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicNodeDTO {
    private Long id;
    private String title;
    private List<SubtopicNodeDTO> subtopics;
}