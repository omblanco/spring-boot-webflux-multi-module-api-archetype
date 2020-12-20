#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ${package}.commons.aop.LoggingAspect;
import ${package}.commons.aop.ProfilingAspect;


/**
 * Configuración para el traceo AOP
 */
@Profile("profiling")
@Configuration
public class ProfilingConfig {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
    
    @Bean
    public ProfilingAspect profilingAspect() {
        return new ProfilingAspect();
    }
    
}
