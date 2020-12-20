#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.configuration;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Profile("!openapi")
public class DisableOpenApiConfig {

    @Bean
    public RouterFunction<ServerResponse> openApiRoutes() {
        return RouterFunctions
                .route(GET("webjars/swagger-ui/index.html").and(accept(APPLICATION_JSON)), request -> ServerResponse.notFound().build());
    }
}
