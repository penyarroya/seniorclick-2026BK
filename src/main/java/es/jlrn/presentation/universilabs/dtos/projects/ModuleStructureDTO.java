package es.jlrn.presentation.universilabs.dtos.projects;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleStructureDTO {
    private Long id;
    private String title;
    private List<PageStructureDTO> pages; 
}
