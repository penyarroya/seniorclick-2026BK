package es.jlrn.presentation.users.Preferences;

import es.jlrn.persistence.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenceResponse {
//
    private Long userId;
    private Integer selectedInstitutionId;
    private String lastPage;
    private Theme theme;
}