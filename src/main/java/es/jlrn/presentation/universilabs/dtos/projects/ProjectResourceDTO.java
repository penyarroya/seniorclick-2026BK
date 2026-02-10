package es.jlrn.presentation.universilabs.dtos.projects;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResourceDTO {
    private String name;
    private String url;
    private String type;
}