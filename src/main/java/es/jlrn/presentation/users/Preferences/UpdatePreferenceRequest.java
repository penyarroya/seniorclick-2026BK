package es.jlrn.presentation.users.Preferences;

import es.jlrn.persistence.enums.Theme;
import jakarta.validation.constraints.Size;
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
public class UpdatePreferenceRequest {
//
    private Integer selectedInstitutionId;

    @Size(max = 255)
    private String lastPage;

    private Theme theme;
}
