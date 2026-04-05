package com.homni.featuretoggle.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    /**
     * Customizes the global Jackson ObjectMapper with modules required by the application.
     *
     * <pre>{@code
     * // JavaTimeModule:      Instant, OffsetDateTime → ISO 8601 strings
     * // JsonNullableModule:  JsonNullable<T> → value or null (not {"present":true})
     * }</pre>
     *
     * @param builder the Spring-provided ObjectMapper builder
     * @return the configured ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .modules(new JavaTimeModule(), new JsonNullableModule())
                .build();
    }
}
