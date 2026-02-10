package es.jlrn.presentation.universilabs.dtos.projects;

import java.util.List;

import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicNodeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageStructureDTO {
//
    private Long id;
    private String title;
    private String content; // Aquí viene el HTML de la DB
    private String type;    // 'video', 'text', etc.
    private List<SubtopicNodeDTO> subtopics;
}
