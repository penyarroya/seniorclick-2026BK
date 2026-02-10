package es.jlrn.presentation.universilabs.dtos.collections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionResponseDTO {
//    
    private Long id;
    private String name;
    private Long projectId;
    private String projectName; // Útil para el frontend
    private Integer topicsCount;
}