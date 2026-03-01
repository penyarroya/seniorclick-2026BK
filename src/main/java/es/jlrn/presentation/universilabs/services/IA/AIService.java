// package es.jlrn.presentation.universilabs.services.IA;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.client.SimpleClientHttpRequestFactory;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.web.client.RestTemplate;

// import es.jlrn.presentation.universilabs.dtos.IA.GeminiResponse;

// import java.util.List;
// import java.util.Map;

// @Service
// public class AIService {
// //
//     @Value("${ai.api.key:}")
//     private String apiKey;

//     // private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";
//     private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=";


//     public String generateResponse(String studentQuestion, String context) {
//         // 1. Configurar RestTemplate con timeouts para evitar bloqueos
//         RestTemplate restTemplate = new RestTemplate();
//         SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//         factory.setConnectTimeout(10000); // 10 segundos para establecer conexión
//         factory.setReadTimeout(10000);    // 10 segundos para recibir respuesta
//         restTemplate.setRequestFactory(factory);

//         // 2. Construir el prompt con el contexto y la pregunta
//         String systemPrompt = "Eres un profesor experto. Responde de forma concisa y profesional. " +
//                             "Contexto de la lección: " + context + ". " +
//                             "Pregunta del alumno: " + studentQuestion;

//         // 3. Crear el cuerpo de la petición en el formato que espera Gemini
//         Map<String, Object> requestBody = Map.of(
//             "contents", List.of(
//                 Map.of("parts", List.of(
//                     Map.of("text", systemPrompt)
//                 ))
//             )
//         );

//         try {
//             // 4. Realizar la llamada a la API de Gemini
//             GeminiResponse response = restTemplate.postForObject(
//                 GEMINI_URL + apiKey, 
//                 requestBody, 
//                 GeminiResponse.class
//             );

//             // 5. Procesar la respuesta si es exitosa
//             if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
//                 return response.candidates().get(0).content().parts().get(0).text();
//             }
            
//             return "No se pudo obtener una respuesta clara de la IA.";

//         } catch (HttpClientErrorException e) {
//             // 6. Manejo específico para errores HTTP (como 429 Too Many Requests)
//             if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
//                 // Mensaje amigable para el usuario cuando se excede la cuota
//                 return "Límite de peticiones a la IA alcanzado. Espera unos minutos y vuelve a intentar.";
//             }
//             // Otros errores HTTP (400, 401, 403, 404, etc.)
//             System.err.println("Error HTTP llamando a Gemini: " + e.getMessage());
//             e.printStackTrace();
//             return "Lo siento, no he podido generar una respuesta en este momento.";

//         } catch (Exception e) {
//             // 7. Cualquier otro error inesperado
//             System.err.println("Error llamando a Gemini: " + e.getMessage());
//             e.printStackTrace();
//             return "Lo siento, no he podido generar una respuesta en este momento.";
//         }
//     }


//     // public String generateResponse(String studentQuestion, String context) {
//     //     RestTemplate restTemplate = new RestTemplate();

//     //     String systemPrompt = "Eres un profesor experto. Responde de forma concisa y profesional. " +
//     //                           "Contexto de la lección: " + context + ". " +
//     //                           "Pregunta del alumno: " + studentQuestion;

//     //     Map<String, Object> requestBody = Map.of(
//     //         "contents", List.of(
//     //             Map.of("parts", List.of(
//     //                 Map.of("text", systemPrompt)
//     //             ))
//     //         )
//     //     );

//     //     try {
//     //         // Usamos la clase GeminiResponse en lugar de Map.class para evitar warnings
//     //         GeminiResponse response = restTemplate.postForObject(GEMINI_URL + apiKey, requestBody, GeminiResponse.class);

//     //         if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
//     //             return response.candidates().get(0).content().parts().get(0).text();
//     //         }
            
//     //         return "No se pudo obtener una respuesta clara de la IA.";

//     //     } catch (Exception e) {
//     //         System.err.println("Error llamando a Gemini: " + e.getMessage());
//     //          e.printStackTrace();
//     //         return "Lo siento, no he podido generar una respuesta en este momento.";
//     //     }
//     // }

//     // --- ESTRUCTURAS PARA MAPEAR EL JSON DE GOOGLE (Sin Warnings) ---
//     // Estas clases representan la jerarquía del JSON de Gemini
//     //     private record GeminiResponse(List<Candidate> candidates) {}
//     //     private record Candidate(Content content) {}
//     //     private record Content(List<Part> parts) {}
//     //     private record Part(String text) {}
//  }



//Google Gemini API Service - Versión mejorada con manejo de errores y configuración flexible

// package es.jlrn.presentation.universilabs.services.IA;

// import es.jlrn.presentation.universilabs.dtos.IA.GeminiResponse;
// import jakarta.annotation.PostConstruct;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.web.client.RestTemplate;

// import java.util.List;
// import java.util.Map;

// @Service
// public class AIService {
// //
//     private static final Logger log = LoggerFactory.getLogger(AIService.class);

//     private final RestTemplate restTemplate;
//     private final String apiKey;
//     private final String geminiUrl;

//     public AIService(
//             RestTemplate restTemplate, 
//             @Value("${ai.api.key}") String apiKey,
//             @Value("${ai.api.url}") String geminiUrl) {
//         this.restTemplate = restTemplate;
//         this.apiKey = (apiKey != null) ? apiKey.trim() : "";
//         this.geminiUrl = geminiUrl;
//     }

//     @PostConstruct
//     private void validateConfig() {
//         if (apiKey.isEmpty() || apiKey.contains("TU_CLAVE")) {
//             throw new IllegalStateException("La API Key no está configurada. Revisa tu application.properties");
//         }
//     }

//     public String generateResponse(String studentQuestion, String context) {
//         String url = geminiUrl + "?key=" + apiKey;
        
//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);

//         HttpEntity<Map<String, Object>> entity = new HttpEntity<>(createGeminiRequest(studentQuestion, context), headers);

//         try {
//             ResponseEntity<GeminiResponse> response = restTemplate.postForEntity(url, entity, GeminiResponse.class);
//             return extractTextFromResponse(response.getBody());
//         } catch (HttpClientErrorException.TooManyRequests e) {
//             log.warn("Límite de API alcanzado (429): {}", e.getMessage());
//             return "El asistente está saturado. Por favor, inténtalo de nuevo en un minuto.";
//         } catch (Exception e) {
//             log.error("Error al conectar con la IA: {}", e.getMessage());
//             return "Lo sentimos, no pudimos procesar tu pregunta en este momento.";
//         }
//     }

//     private Map<String, Object> createGeminiRequest(String question, String context) {
//         String fullPrompt = String.format(
//             "Eres un profesor experto. Responde de forma concisa, profesional y en español.\n" +
//             "Contexto de la lección: %s\n" +
//             "Pregunta del alumno: %s", 
//             context, question
//         );

//         return Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", fullPrompt)))));
//     }

//     private String extractTextFromResponse(GeminiResponse response) {
//         return (response != null && !response.candidates().isEmpty()) 
//             ? response.candidates().get(0).content().parts().get(0).text() 
//             : "La IA no devolvió una respuesta válida.";
//     }
// }


package es.jlrn.presentation.universilabs.services.IA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;
    private final String modelName;

    public AIService(
            RestTemplate restTemplate, 
            @Value("${ai.api.key}") String apiKey,
            @Value("${ai.api.url}") String apiUrl,
            @Value("${ai.model.name}") String modelName) {
        this.restTemplate = restTemplate;
        this.apiKey = (apiKey != null) ? apiKey.trim() : "";
        this.apiUrl = apiUrl;
        this.modelName = modelName;
    }

    @PostConstruct
    private void validateConfig() {
        if (apiKey.isEmpty() || apiKey.contains("tu-clave-aqui")) {
            log.error("¡ERROR CRÍTICO: La API Key de OpenRouter no está configurada correctamente!");
        }
    }

    /**
     * Genera una respuesta adaptada para personas mayores, estrictamente ceñida al contexto.
     */
    public String generateResponse(String studentQuestion, String context) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey); 
        
        headers.set("HTTP-Referer", "https://universilabs.edu.es");
        headers.set("X-Title", "SeniorClick - UniversiLabs");

        Map<String, Object> body = createOpenRouterRequest(studentQuestion, context);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("Solicitando explicación a IA sobre tema: {} - Modelo: {}", context, modelName);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                entity, 
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return extractTextFromResponse(response.getBody());

        } catch (HttpClientErrorException e) {
            log.error("Error de OpenRouter ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Lo siento, el servicio de ayuda está temporalmente saturado. Por favor, inténtalo de nuevo en unos minutos.";
        } catch (Exception e) {
            log.error("Error inesperado en AIService: {}", e.getMessage());
            return "Hubo un problema al conectar con el profesor digital.";
        }
    }

    /**
     * Configura el prompt del sistema para que actúe como filtro de contexto.
     */
    private Map<String, Object> createOpenRouterRequest(String question, String context) {
        // Prompt con lógica de control de contexto para SeniorClick
        String systemPrompt = String.format(
                "Eres un profesor experto de 'SeniorClick', especializado en alfabetización digital para personas mayores. " +
                "Tu misión es ayudar a los alumnos EXCLUSIVAMENTE con el tema de la lección: '%s'. " +
                "\n\nREGLAS DE ORO:" +
                "\n1. Si la pregunta del alumno NO tiene relación con la informática o específicamente con el tema '%s', " +
                "responde de forma muy amable: '¡Hola! Como tu profesor de SeniorClick, solo puedo ayudarte con dudas sobre esta lección de %s. ¿Tienes alguna pregunta sobre esto?'" +
                "\n2. Si la pregunta es pertinente, responde de forma concisa (máximo 3 párrafos) y con lenguaje muy sencillo." +
                "\n3. Evita tecnicismos. Si usas uno, explícalo con una metáfora de la vida cotidiana." +
                "\n4. Tu tono debe ser paciente, cercano y siempre en español.",
                context, context, context
        );

        Map<String, Object> request = new HashMap<>();
        request.put("model", modelName); 
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", question));
        
        request.put("messages", messages);
        
        // Temperatura baja (0.4) para evitar que la IA se invente cosas o se distraiga del tema
        request.put("temperature", 0.4);
        return request;
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> response) {
        try {
            if (response == null) return "No se recibió contenido de la IA.";

            if (response.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) response.get("error");
                log.warn("OpenRouter devolvió un error en el JSON: {}", error.get("message"));
                return "El profesor digital no está disponible ahora mismo.";
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                
                if (message != null && message.containsKey("content")) {
                    String rawContent = (String) message.get("content");
                    return rawContent != null ? rawContent.trim() : "Respuesta vacía.";
                }
            }
        } catch (Exception e) {
            log.error("Fallo al parsear la respuesta de la IA: {}", e.getMessage());
        }
        return "No se pudo procesar la explicación de la IA.";
    }

    @PostConstruct
    public void testConfig() {
        log.info("### CONFIGURACIÓN DE IA CARGADA ###");
        log.info("Modelo activo: {}", modelName);
        log.info("Target URL: {}", apiUrl);
        log.info("Context Filtering: ENABLED");
        log.info("###################################");
    }
}