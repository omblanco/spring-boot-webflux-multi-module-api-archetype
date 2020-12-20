#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.web.controllers;

import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V2;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V3;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import ${package}.app.web.dto.UserDTO;
import ${package}.commons.web.utils.BaseApiConstants;
import ${package}.service.user.UserBO;
import ${package}.service.user.UserService;

import reactor.core.publisher.Mono;

/**
 * Test de integración para el UserController
 * @author oscar.martinezblanco
 * 
 * see https://www.baeldung.com/parameterized-tests-junit-5
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
public class UserControllerTests {
    
    @Autowired
    private WebTestClient client;
    
    @Autowired
    private UserService<Long> userService;
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void findAllTest(String path) throws Exception {
        client.get()
        .uri(path)
        .accept(MediaType.APPLICATION_JSON)
        .exchange().expectStatus()
        .isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(UserDTO.class)
        .consumeWith(response -> {
            List<UserDTO> users = response.getResponseBody();
            users.forEach(user -> {
                System.out.println(user);
            });
            
            Assertions.assertThat(users.size() > 0).isTrue();
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void findFyFilterTest(String path) {
        String name = "Maria";
        Integer page = 0;
        Integer size = 10;
        
        client.get().uri(uriBuilder ->
            uriBuilder
            .path(path)
            .queryParam("page", page)
            .queryParam("size", size)
            .queryParam("name", name)
            .build())
        .accept(MediaType.APPLICATION_JSON)
        .exchange().expectStatus()
        .isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("${symbol_dollar}.numberOfElements").isNotEmpty()
        .jsonPath("${symbol_dollar}.numberOfElements").isEqualTo(1)
        .jsonPath("${symbol_dollar}.content").isArray()
        .jsonPath("${symbol_dollar}.content.length()").isEqualTo(1)
        .jsonPath("${symbol_dollar}.content[0].name").isEqualTo(name);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void getByidTest(String path) {
        UserBO<Long> user = userService.findAll().blockFirst();
        
        client.get()
        .uri(path.concat("/{id}"), Collections.singletonMap("id", user.getId()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDTO.class)
        .consumeWith(response -> {
            UserDTO userResponse = response.getResponseBody();
            Assertions.assertThat(userResponse.getId()).isNotNull();
            Assertions.assertThat(userResponse.getId() > 0).isTrue();
            Assertions.assertThat(userResponse.getId()).isEqualTo(user.getId());
            Assertions.assertThat(userResponse.getBirthdate()).isEqualTo(user.getBirthdate());
            Assertions.assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
            Assertions.assertThat(userResponse.getName()).isEqualTo(user.getName());
            Assertions.assertThat(userResponse.getSurname()).isEqualTo(user.getSurname());
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void postTest(String path) {
        UserDTO user = new UserDTO(null, "Fulano", "De tal", "fulano@mail.com", new Date(), "1234");
        
        client.post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(user), UserDTO.class)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDTO.class).consumeWith(response -> {
            UserDTO userResponse = response.getResponseBody();
            Assertions.assertThat(userResponse.getId()).isNotNull();
            Assertions.assertThat(userResponse.getId() > 0).isTrue();
            Assertions.assertThat(userResponse.getBirthdate()).isEqualTo(user.getBirthdate());
            Assertions.assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
            Assertions.assertThat(userResponse.getName()).isEqualTo(user.getName());
            Assertions.assertThat(userResponse.getSurname()).isEqualTo(user.getSurname());
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void postWithValidationErrorsTest(String path) {
        UserDTO user = new UserDTO(null, "F", "De", "fu", null, "1234");
        
        client.post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(user), UserDTO.class)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("${symbol_dollar}.code").isNotEmpty()
        .jsonPath("${symbol_dollar}.code").isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.name())
        .jsonPath("${symbol_dollar}.message").isNotEmpty()
        .jsonPath("${symbol_dollar}.message").value(message -> {
            Assertions.assertThat(message.toString()).contains("Validation failure");
        })
        .jsonPath("${symbol_dollar}.errors").isArray()
        .jsonPath("${symbol_dollar}.errors.length()").isEqualTo(5);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void putTest(String path) {
        UserBO<Long> user = userService.findAll().blockFirst();
        
        user.setEmail("email@mail.com");
        user.setName("Name");
        user.setSurname("Surname");
        user.setPassword("1234");
        
        client.put()
        .uri(path.concat("/{id}"), Collections.singletonMap("id", user.getId()))
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(user), UserDTO.class)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDTO.class)
        .consumeWith(response -> {
            UserDTO userResponse = response.getResponseBody();
            Assertions.assertThat(userResponse.getId()).isNotNull();
            Assertions.assertThat(userResponse.getId() > 0).isTrue();
            Assertions.assertThat(userResponse.getId()).isEqualTo(user.getId());
            Assertions.assertThat(userResponse.getBirthdate()).isEqualTo(user.getBirthdate());
            Assertions.assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
            Assertions.assertThat(userResponse.getName()).isEqualTo(user.getName());
            Assertions.assertThat(userResponse.getSurname()).isEqualTo(user.getSurname());
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2, USER_BASE_URL_V3})
    public void deleteTest(String path) {
        UserBO<Long> user = userService.findAll().blockFirst();
        client.delete()
            .uri(path.concat("/{id}"), Collections.singletonMap("id", user.getId()))
            .exchange()
            .expectStatus().isNoContent()
            .expectBody().isEmpty();
        
        client.get()
            .uri(BaseApiConstants.USER_BASE_URL_V1.concat("/{id}"), Collections.singletonMap("id", user.getId()))
            .exchange()
            .expectStatus().isNotFound()
            .expectBody().isEmpty();
    }
}
