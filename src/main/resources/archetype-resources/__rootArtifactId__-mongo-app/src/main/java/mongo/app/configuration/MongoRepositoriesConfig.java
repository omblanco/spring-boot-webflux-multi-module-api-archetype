#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

/**
 * Clase de configuración para habilitar los repositorios de mongo
 * @author oscar.martinezblanco
 *
 */
@Configuration
@EnableReactiveMongoRepositories(basePackages = {"${package}.model"})
public class MongoRepositoriesConfig {

}
