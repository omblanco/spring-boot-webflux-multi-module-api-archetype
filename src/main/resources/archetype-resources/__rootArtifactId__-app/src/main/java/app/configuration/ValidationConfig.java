#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import ${package}.commons.web.errorhandling.RestValidationExceptionHandler;

/**
 * Configuración para la validación de modelos
 * @author oscar.martinezblanco
 *
 */
@Configuration
public class ValidationConfig {

    /**
     * Bean de validación de dtos
     * @param objectMapper Serializa y deserializa objetos
     * @return Handler de validación
     */
    @Bean
    @Order(-2)
    public RestValidationExceptionHandler getRestValidationExceptionHandler(ObjectMapper objectMapper) {
        return new RestValidationExceptionHandler(objectMapper);
    }
}
