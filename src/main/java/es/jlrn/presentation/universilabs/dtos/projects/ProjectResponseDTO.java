package es.jlrn.presentation.universilabs.dtos.projects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDTO {
    private Long id;
    private String title;
    private String description;
    private int level;
    private Long institutionId;
    private String institutionName;
    private String creatorName;
    private boolean activo;
}