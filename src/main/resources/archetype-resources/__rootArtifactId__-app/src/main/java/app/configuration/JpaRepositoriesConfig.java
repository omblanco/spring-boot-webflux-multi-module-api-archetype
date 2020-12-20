#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase de configuración para los repositorios de JPA
 * @author oscar.martinezblanco
 *
 */
@Configuration
@EntityScan(basePackages = {"${package}.model"})
@EnableJpaRepositories(basePackages = "${package}.model")
public class JpaRepositoriesConfig {

}
