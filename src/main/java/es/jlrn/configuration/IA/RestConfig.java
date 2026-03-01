package es.jlrn.configuration.IA;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    @Bean
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5s es más estándar para APIs externas
        factory.setReadTimeout(15000);    // 15s porque la IA puede ser lenta
        return new RestTemplate(factory);
    }
}