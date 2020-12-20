#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para el escaneo de repositorios
 * @author oscar.martinezblanco
 *
 */
@Configuration
@ComponentScan(basePackages = {
        "${package}.model"
})
public class RepositoriesConfig {

}
