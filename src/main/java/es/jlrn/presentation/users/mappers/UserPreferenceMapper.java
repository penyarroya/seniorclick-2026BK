package es.jlrn.presentation.users.mappers;

import es.jlrn.persistence.enums.Theme;
import es.jlrn.persistence.models.users.models.UserPreference;
import es.jlrn.presentation.users.Preferences.PreferenceResponse;
import es.jlrn.presentation.users.Preferences.UpdatePreferenceRequest;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserPreferenceMapper {
//
    /**
     * Convierte la entidad a DTO.
     */
    public PreferenceResponse toResponse(UserPreference pref) {
        if (pref == null) return null;

        return PreferenceResponse.builder()
                .userId(pref.getUserId())
                .selectedInstitutionId(pref.getSelectedInstitutionId())
                .lastPage(pref.getLastPage())
                .theme(pref.getTheme() != null ? pref.getTheme() : Theme.LIGHT)
                .build();
    }

    /**
     * Actualiza la entidad existente a partir del DTO de request.
     * Solo aplica cambios si los valores no son null.
     */
    public UserPreference fromUpdateRequest(UpdatePreferenceRequest request, UserPreference existing) {
        if (existing == null) throw new IllegalArgumentException("Existing UserPreference cannot be null");
        if (request == null) return existing;

        // Institución seleccionada
        if (request.getSelectedInstitutionId() != null && !Objects.equals(request.getSelectedInstitutionId(), existing.getSelectedInstitutionId())) {
            existing.setSelectedInstitutionId(request.getSelectedInstitutionId());
        }

        // Última página
        if (request.getLastPage() != null && !request.getLastPage().isBlank()) {
            String trimmed = request.getLastPage().trim();
            if (!trimmed.equals(existing.getLastPage())) {
                existing.setLastPage(trimmed);
            }
        }

        // Tema
        if (request.getTheme() != null && request.getTheme() != existing.getTheme()) {
            existing.setTheme(request.getTheme());
        }

        return existing;
    }
}
