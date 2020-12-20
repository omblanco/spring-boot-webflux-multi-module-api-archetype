#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app.configuration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Desactiva el acceso a la interfaz web de swagger
 * @author ombla
 *
 */
@Configuration
@Profile("!swagger")
public class DisableSwaggerConfig {
    
    @Bean
    public RouterFunction<ServerResponse> swaggerRoutes() {
        return RouterFunctions
                .route(GET("swagger-ui/index.html").and(accept(APPLICATION_JSON)), request -> ServerResponse.notFound().build());
    }
}
