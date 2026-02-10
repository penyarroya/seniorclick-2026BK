package es.jlrn.presentation.universilabs.dtos.subtopics;

import lombok.*;
import java.util.List;

import es.jlrn.presentation.universilabs.dtos.pages.PageSummaryNodeDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtopicNodeDTO {
    private Long id;
    private String title;
    private List<PageSummaryNodeDTO> pages;
}