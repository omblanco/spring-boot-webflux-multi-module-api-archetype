#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ${package}.commons.security.AuthenticationManager;
import ${package}.commons.security.SecurityContextRepository;
import ${package}.commons.security.TokenProvider;

/**
 * Clase de configuración con los beans necesarios para la autenticación
 * @author oscar.martinezblanco
 *
 */
@Configuration
public class SecurityConfig {

    @Bean
    AuthenticationManager authenticationManager(TokenProvider tokenProvider) {
        return new AuthenticationManager(tokenProvider);
    }
    
    @Bean
    TokenProvider tokenProvider(PropertyResolver propertyResolver) {
        return new TokenProvider(propertyResolver);
    }
    
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    SecurityContextRepository securityContextRepository(AuthenticationManager authenticationManager) {
        return new SecurityContextRepository(authenticationManager);
    }
}
