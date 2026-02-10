package es.jlrn.presentation.universilabs.dtos.projects;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectStructureDTO {
    private Long id;
    private String title;
    private String description;
    private String institutionName;
    private List<ModuleStructureDTO> modules; // La lista de módulos con sus páginas
}