package es.jlrn.presentation.universilabs.dtos.collections;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionRequestDTO {
//    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    @NotNull(message = "El ID del proyecto es obligatorio")
    private Long projectId;
}