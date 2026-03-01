package es.jlrn.presentation.universilabs.dtos.IA;

import java.util.List;

/**
 * DTO principal para la respuesta de Gemini con sus hijos anidados
 */
public record GeminiResponse(List<Candidate> candidates) {
    
    // Al estar dentro de GeminiResponse, pueden ser public sin dar error de archivo
    public record Candidate(Content content) {}

    public record Content(List<Part> parts) {}

    public record Part(String text) {}
}