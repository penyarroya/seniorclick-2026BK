package es.jlrn.presentation.universilabs.dtos.collections;

import lombok.*;
import java.util.List;

import es.jlrn.presentation.universilabs.dtos.topics.TopicNodeDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionNodeDTO {
    private Long id;
    private String name; // Coincide con tu interfaz de Angular
    private List<TopicNodeDTO> topics;
}