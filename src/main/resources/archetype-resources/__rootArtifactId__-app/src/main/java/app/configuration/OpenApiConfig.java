#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.configuration;

import static ${package}.commons.web.utils.BaseApiConstants.AUTH_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V2;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V3;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;

/**
 * Clase de configuración de la docupentación Open Api de la Api REST
 * @author ombla
 *
 */
@Profile("openapi")
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${symbol_dollar}{app.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.APIKEY).name("Authorization").bearerFormat("JWT").in(In.HEADER)))
                .info(new Info().title("Reactive Users API").version(appVersion)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi usersV1OpenApi() {
        String[] paths = { USER_BASE_URL_V1.concat("/**") };
        return GroupedOpenApi.builder().group("Users V1").pathsToMatch(paths)
                .build();
    }
    
    
    @Bean
    public GroupedOpenApi usersV2OpenApi() {
        String[] paths = { USER_BASE_URL_V2.concat("/**") };
        return GroupedOpenApi.builder().group("Users V2").pathsToMatch(paths)
                .build();
    }
    
    
    @Bean
    public GroupedOpenApi usersV3OpenApi() {
        String[] paths = { USER_BASE_URL_V3.concat("/**") };
        return GroupedOpenApi.builder().group("Users V3").pathsToMatch(paths)
                .build();
    }
    
    @Bean
    public GroupedOpenApi loginOpenApi() {
        String[] paths = { AUTH_URL_V1.concat("/**") };
        return GroupedOpenApi.builder().group("Login").pathsToMatch(paths)
                .build();
    }    
}
;