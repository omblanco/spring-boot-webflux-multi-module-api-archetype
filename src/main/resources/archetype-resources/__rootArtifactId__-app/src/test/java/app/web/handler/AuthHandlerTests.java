#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.web.handler;

import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V2;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V3;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import ${package}.commons.web.utils.BaseApiConstants;
import ${package}.commons.web.dto.login.LoginRequestDTO;
import ${package}.commons.web.dto.login.LoginResponseDTO;

import reactor.core.publisher.Mono;

/**
 * Test de integración para el AuthHandler
 * @author oscar.martinezblanco
 * 
 * see https://www.baeldung.com/parameterized-tests-junit-5
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
@ActiveProfiles(profiles = "security")
public class AuthHandlerTests {

    @Autowired
    private WebTestClient client;
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void testWithSecurity(String path) {
        
        client.get()
        .uri(path)
        .accept(MediaType.APPLICATION_JSON)
        .exchange().expectStatus()
        .isUnauthorized();
        
        LoginRequestDTO login = new LoginRequestDTO();
        login.setEmail("john@mail.com");
        login.setPassword("1234");
        
        LoginResponseDTO loginResponse = client.post()
            .uri(BaseApiConstants.AUTH_URL_V1)
            .accept(MediaType.APPLICATION_JSON)
            .body(Mono.just(login), LoginRequestDTO.class)
            .exchange().expectStatus()
            .isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(LoginResponseDTO.class)
            .returnResult().getResponseBody();
        
        Assertions.assertThat(loginResponse.getToken()).isNotNull();
        Assertions.assertThat(loginResponse.getToken()).isNotEmpty();
        Assertions.assertThat(loginResponse.getToken().length() == 173).isTrue();

        client.get()
            .uri(path)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.getToken())
            .accept(MediaType.APPLICATION_JSON)
            .exchange().expectStatus()
            .isOk();
    }
    
}
