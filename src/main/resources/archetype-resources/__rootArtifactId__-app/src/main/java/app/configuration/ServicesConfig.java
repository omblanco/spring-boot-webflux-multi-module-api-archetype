#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para el escaneo de servicios
 * @author oscar.martinezblanco
 *
 */
@Configuration
@ComponentScan(basePackages = {
        "${package}.service"
})
public class ServicesConfig {

}
