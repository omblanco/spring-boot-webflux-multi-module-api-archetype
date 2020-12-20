#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de configuración de Spring Boot
 * @author oscar.martinezblanco
 *
 */
@SpringBootApplication
public class SpringBootWebfluxSimpleApiMongoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxSimpleApiMongoAppApplication.class, args);
	}
}
