#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.configuration;

import static ${package}.commons.web.utils.BaseApiConstants.AUTH_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.STATUS_BASE_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V2;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V3;
import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author oscar.martinezblanco
 *
 * see http://springfox.github.io/springfox/docs/snapshot/${symbol_pound}springfox-spring-data-rest
 * http://localhost:8080/swagger-ui/index.html
 */
@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {

    @Value("${symbol_dollar}{app.version}")
    private String appVersion;
    
    @Bean
    public Docket docketUsersV1() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.usersApiInfoV1())
                .enable(true)
                .groupName("user-api-v1")
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .paths(userPathsV1())
                .build();
    }
    
    private ApiInfo usersApiInfoV1() {
        return new ApiInfoBuilder()
                .title("Reactive Users V1")
                .description("Reactive API V1 para usuarios desarrollada con @Controller")
                .version(appVersion)
                .build();
    }

    
    private Predicate<String> userPathsV1() {
        return regex(USER_BASE_URL_V1.concat(".*"));
    }
    
    @Bean
    public Docket docketUsersV2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.usersApiInfoV2())
                .enable(true)
                .groupName("user-api-v2")
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .paths(userPathsV2())
                .build();
    }
    
    private ApiInfo usersApiInfoV2() {
        return new ApiInfoBuilder()
                .title("Reactive Users V2")
                .description("Reactive API V2 para usuarios desarrollada con @RestController")
                .version(appVersion)
                .build();
    }

    
    private Predicate<String> userPathsV2() {
        return regex(USER_BASE_URL_V2.concat(".*"));
    }
    
    @Bean
    public Docket docketUsersV3() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.usersApiInfoV3())
                .enable(true)
                .groupName("user-api-v3")
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .paths(userPathsV3())
                .build();
    }
    
    private ApiInfo usersApiInfoV3() {
        return new ApiInfoBuilder()
                .title("Reactive Users V3")
                .description("Reactive API V3 para usuarios desarrollada con Functional Endpoints")
                .version(appVersion)
                .build();
    }

    
    private Predicate<String> userPathsV3() {
        return regex(USER_BASE_URL_V3.concat(".*"));
    }
    
    @Bean
    public Docket docketAppInfo() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.appInfoapiInfo())
                .enable(true)
                .groupName("app-info-api")
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .paths(AppInfoPaths())
                .build();
    }
    
    private ApiInfo appInfoapiInfo() {
        return new ApiInfoBuilder()
                .title("Reactive App Info")
                .description("Reactive API V1 para recuperar la información de la aplicación desarrollada con @RestController")
                .version(appVersion)
                .build();
    }

    private Predicate<String> AppInfoPaths() {
        return regex(STATUS_BASE_URL_V1.concat(".*"));
    }
    
    private ApiKey apiKey() {
        return new ApiKey("JWT", HttpHeaders.AUTHORIZATION, "header");
    }
    
    private SecurityContext securityContext() {
        Predicate<String> paths = PathSelectors.regex(USER_BASE_URL_V1.concat(".*"))
                .or(PathSelectors.regex(USER_BASE_URL_V2.concat(".*")))
                .or(PathSelectors.regex(USER_BASE_URL_V3.concat(".*")))
                .or(PathSelectors.regex(STATUS_BASE_URL_V1.concat(".*")));
        
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(paths)
            .build();
    }
    
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
    
    @Bean
    public Docket docketAuthHandler() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.authHandlerApiInfo())
                .enable(true)
                .groupName("app-auth-api")
                .select()
                .paths(authHandlerPaths())
                .build();
    }
    
    private ApiInfo authHandlerApiInfo() {
        return new ApiInfoBuilder()
                .title("Reactive Auth")
                .description("Reactive Auth para obtener un token de autenticación")
                .version(appVersion)
                .build();
    }

    private Predicate<String> authHandlerPaths() {
        return regex(AUTH_URL_V1.concat(".*"));
    }    
}