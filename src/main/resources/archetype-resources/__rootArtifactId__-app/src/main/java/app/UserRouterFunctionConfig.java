#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app;

import static ${package}.commons.web.utils.BaseApiConstants.AUTH_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.ID_PARAM_URL;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V3;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import ${package}.app.web.dto.UserDTO;
import ${package}.app.web.handler.UserHandler;
import ${package}.commons.web.dto.login.LoginRequestDTO;
import ${package}.commons.web.dto.login.LoginResponseDTO;
import ${package}.commons.web.handler.login.AuthHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Configuración para los Functional Endpoints de usuario
 * @author oscar.martinezblanco
 * see https://springdoc.org/${symbol_pound}spring-webfluxwebmvc-fn-with-functional-endpoints
 * see https://github.com/springdoc/springdoc-openapi-demos
 * see https://github.com/springdoc/springdoc-openapi
 * see https://github.com/springdoc/springdoc-openapi/blob/master/springdoc-openapi-webflux-core/src/test/java/test/org/springdoc/api/app90/book/BookRouter.java
 */
@Configuration
public class UserRouterFunctionConfig {

    @Autowired
    private UserHandler userHandler; 
    
    @Autowired
    private AuthHandler authHandler;
    
    @RouterOperations({ 
        @RouterOperation(
                path = USER_BASE_URL_V3,
                method = { RequestMethod.GET},
                operation = @Operation(operationId = "getUsers", summary = "Get users", tags = { "Users" },
                parameters = {
                        @Parameter(in = ParameterIn.QUERY, description = "Number page", name = "page"),
                        @Parameter(in = ParameterIn.QUERY, description = "Size page", name = "size"),
                        @Parameter(in = ParameterIn.QUERY, description = "Name", name = "name"),
                        @Parameter(in = ParameterIn.QUERY, description = "Surname", name = "surname"),
                        @Parameter(in = ParameterIn.QUERY, description = "Email", name = "email"),
                        @Parameter(in = ParameterIn.QUERY, description = "Sort", name = "sort")
                },
                responses = { 
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))),
                        @ApiResponse(responseCode = "401", description = "Invalid token"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
                })
        ),
        @RouterOperation(
                path = USER_BASE_URL_V3 + ID_PARAM_URL,
                method = { RequestMethod.GET},
                operation = @Operation(operationId = "getUser", summary = "Get users", tags = { "Users" },
                parameters = {
                        @Parameter(in = ParameterIn.PATH, description = "User id", name = "id", required = true)
                },
                responses = { 
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid token"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
                })
        ),
        @RouterOperation(
                path = USER_BASE_URL_V3,
                method = { RequestMethod.POST},
                operation = @Operation(operationId = "saveUser", summary = "Save user", tags = { "Users" },
                requestBody = @RequestBody(required = true, description = "User" , content = @Content(schema = @Schema(implementation = UserDTO.class))),
                responses = { 
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid token"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
                })
        ),
        @RouterOperation(
                path = USER_BASE_URL_V3 + ID_PARAM_URL,
                method = { RequestMethod.PUT},
                operation = @Operation(operationId = "updateUser", summary = "Update user", tags = { "Users" },
                parameters = {
                        @Parameter(in = ParameterIn.PATH, description = "User id", name = "id", required = true)
                },
                requestBody = @RequestBody(required = true, description = "User" , content = @Content(schema = @Schema(implementation = UserDTO.class))),
                responses = { 
                        @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = UserDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid token"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
                })
        ),
        @RouterOperation(
                path = USER_BASE_URL_V3 + ID_PARAM_URL,
                method = { RequestMethod.DELETE},
                operation = @Operation(operationId = "deleteUser", summary = "Delete user", tags = { "Users" },
                parameters = {
                        @Parameter(in = ParameterIn.PATH, description = "User id", name = "id", required = true)
                },
                responses = { 
                        @ApiResponse(responseCode = "204", description = "Successful operation"),
                        @ApiResponse(responseCode = "404", description = "User not found"),
                        @ApiResponse(responseCode = "401", description = "Invalid token"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
                })
        )
    })
    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route(GET(USER_BASE_URL_V3), userHandler::findAll)
                .andRoute(GET(USER_BASE_URL_V3.concat(ID_PARAM_URL)), userHandler::get)
                .andRoute(POST(USER_BASE_URL_V3).and(contentType(APPLICATION_JSON)), userHandler::create)
                .andRoute(PUT(USER_BASE_URL_V3.concat(ID_PARAM_URL)).and(contentType(APPLICATION_JSON)), userHandler::update)
                .andRoute(DELETE(USER_BASE_URL_V3.concat(ID_PARAM_URL)), userHandler::delete);
    }
    
    @RouterOperations({ 
        @RouterOperation(
                path = AUTH_URL_V1,
                method = { RequestMethod.POST},
                operation = @Operation(operationId = "login", summary = "Get token", tags = { "Login" },
                requestBody = @RequestBody(required = true, description = "Email and password user" , content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))),
                responses = { 
                        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid email o password")
                })
        )
    })
    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return RouterFunctions
                .route(POST(AUTH_URL_V1).and(accept(APPLICATION_JSON)), authHandler::login);
    }
}
