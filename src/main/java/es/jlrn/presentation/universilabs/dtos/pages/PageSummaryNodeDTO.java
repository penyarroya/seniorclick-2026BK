package es.jlrn.presentation.universilabs.dtos.pages;

import java.util.List;

import es.jlrn.presentation.universilabs.dtos.projects.ProjectResourceDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageSummaryNodeDTO {
    private Long id;
    private String title;
    private List<ProjectResourceDTO> resources;
}