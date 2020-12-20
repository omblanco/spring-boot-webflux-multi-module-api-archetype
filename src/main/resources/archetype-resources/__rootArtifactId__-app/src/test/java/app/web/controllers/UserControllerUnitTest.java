#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.web.controllers;

import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V1;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import ${package}.app.configuration.ModelMapperConfig;
import ${package}.app.configuration.SecurityConfig;
import ${package}.app.configuration.SecurityWebFilterChainConfig;
import ${package}.app.web.dto.UserDTO;
import ${package}.commons.web.dto.user.UserFilterDTO;
import ${package}.model.entity.user.UserDAO;
import ${package}.model.entity.user.UserFilterDAO;
import ${package}.model.repository.user.UserRepository;
import ${package}.service.user.UserBO;
import ${package}.service.user.UserFilterBO;
import ${package}.service.user.UserServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Test unitarios para los controllers
 * @author oscar.martinezblanco
 *
 *  see https://www.baeldung.com/parameterized-tests-junit-5
 *
 */
@WebFluxTest(controllers = {UserController.class, UserRestController.class})
@Import({UserServiceImpl.class, ModelMapperConfig.class, SecurityConfig.class, SecurityWebFilterChainConfig.class})
public class UserControllerUnitTest {
    
    @MockBean
    private UserRepository<Long> userRepository;
    
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;
    
    @MockBean
    private ModelMapper modelMapper;
    
    @MockBean
    private SharedEntityManagerCreator entityManagerFactory;
    
    @Autowired
    WebTestClient webTestClient;
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2})
    public void findAllTest(String path) throws Exception {
        //given:
        UserDAO<Long> user1 = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDAO<Long> user2 = new UserDAO<Long>(1L, "Mary", "Queen", "mary@mail.com", new Date(), "1234");
        
        UserBO<Long> userBo1 = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo2 = new UserBO<Long>(2L, "Mary", "Queen", "mary@mail.com", new Date(), "1234");
        
        UserDTO userDTO1 = new UserDTO(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDTO userDTO2 = new UserDTO(2L, "Mary", "Queen", "mary@mail.com", new Date(), "1234");
        
        //when:
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo1, userBo2);
        when(modelMapper.map(userBo1, UserDTO.class)).thenReturn(userDTO1);
        when(modelMapper.map(userBo2, UserDTO.class)).thenReturn(userDTO2);
        
        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));
        webTestClient
            .get().uri(path)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserDTO.class)
            .consumeWith(response -> {
                List<UserDTO> users = response.getResponseBody();
                
                //then:
                assertThat(users.size() == 2).isTrue();
                assertThat(userDTO1.getId()).isEqualTo(users.get(0).getId());
                assertThat(userDTO1.getName()).isEqualTo(users.get(0).getName());
                assertThat(userDTO1.getSurname()).isEqualTo(users.get(0).getSurname());
                assertThat(userDTO2.getId()).isEqualTo(users.get(1).getId());
                assertThat(userDTO2.getName()).isEqualTo(users.get(1).getName());
                assertThat(userDTO2.getSurname()).isEqualTo(users.get(1).getSurname());
            });
        
        //then:
        verify(userRepository, times(1)).findAll();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2})
    public void findFyFilterTest(String path) {
        //given
        UserDAO<Long> userDao = new UserDAO<Long>(1L, "Maria", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo = new UserBO<Long>(1L, "Maria", "Doe", "john@mail.com", new Date(), "1234");
        UserDTO userDto = new UserDTO(1L, "Maria", "Doe", "john@mail.com", new Date(), "1234");
        
        String name = "Maria";
        Integer page = 0;
        Integer size = 10;
        
        Page<UserDAO<Long>> pageUsers = new PageImpl<UserDAO<Long>>(Arrays.asList(userDao));
        
        //when
        when(userRepository.findAll(ArgumentMatchers.any(UserFilterDAO.class), ArgumentMatchers.any()))
            .thenReturn(Mono.just(pageUsers));
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        when(modelMapper.map(userBo, UserDTO.class)).thenReturn(userDto);
        when(modelMapper.map(ArgumentMatchers.any(UserFilterDTO.class), ArgumentMatchers.any())).thenReturn(new UserFilterBO());
        when(modelMapper.map(ArgumentMatchers.any(UserFilterBO.class), ArgumentMatchers.any())).thenReturn(new UserFilterDAO());
        
        webTestClient.get().uri(uriBuilder ->
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
         //then:
        .jsonPath("${symbol_dollar}.numberOfElements").isNotEmpty()
        .jsonPath("${symbol_dollar}.numberOfElements").isEqualTo(1)
        .jsonPath("${symbol_dollar}.content").isArray()
        .jsonPath("${symbol_dollar}.content.length()").isEqualTo(1)
        .jsonPath("${symbol_dollar}.content[0].name").isEqualTo(name);
        
        //then:
        verify(userRepository, times(1)).findAll(ArgumentMatchers.any(UserFilterDAO.class), ArgumentMatchers.any());
    }    
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2})
    public void getByidTest(String path) {
        //given:
        UserDAO<Long> userDao = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDTO userDto = new UserDTO(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        //when:
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        when(modelMapper.map(userBo, UserDTO.class)).thenReturn(userDto);
        when(userRepository.findById(userDao.getId())).thenReturn(Mono.just(userDao));
        
        //then:
        webTestClient.get()
        .uri(path.concat("/{id}"), Collections.singletonMap("id", userDao.getId()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDTO.class)
        .consumeWith(response -> {
            
            //then:
            UserDTO userResponse = response.getResponseBody();
            assertThat(userResponse.getId()).isNotNull();
            assertThat(userResponse.getId() > 0).isTrue();
            assertThat(userResponse.getId()).isEqualTo(userDto.getId());
            assertThat(userResponse.getBirthdate()).isEqualTo(userDto.getBirthdate());
            assertThat(userResponse.getEmail()).isEqualTo(userDto.getEmail());
            assertThat(userResponse.getName()).isEqualTo(userDto.getName());
            assertThat(userResponse.getSurname()).isEqualTo(userDto.getSurname());
        });
        
        //then:
        verify(userRepository, times(1)).findById(userDao.getId());
    }
    
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2})
    public void postTest(String path) {
        //given:
        UserDTO userDtoRequest = new UserDTO(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDAO<Long> userDao = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        //when
        when(modelMapper.map(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        when(modelMapper.map(ArgumentMatchers.any(UserBO.class), ArgumentMatchers.any(Type.class))).thenReturn(userDao);
        
        when(modelMapper.map(userBo, UserDTO.class)).thenReturn(userDtoRequest);
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        
        when(userRepository.save(ArgumentMatchers.any(UserDAO.class))).thenReturn(Mono.just(userDao));
        when(passwordEncoder.encode(userDtoRequest.getPassword())).thenReturn("encodePassword");
        webTestClient.post()
        .uri(path)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(userDtoRequest), UserDTO.class)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDTO.class).consumeWith(response -> {
            
            //then:
            UserDTO userResponse = response.getResponseBody();
            Assertions.assertThat(userResponse.getId()).isNotNull();
            Assertions.assertThat(userResponse.getId() > 0).isTrue();
            Assertions.assertThat(userResponse.getBirthdate()).isEqualTo(userDao.getBirthdate());
            Assertions.assertThat(userResponse.getEmail()).isEqualTo(userDao.getEmail());
            Assertions.assertThat(userResponse.getName()).isEqualTo(userDao.getName());
            Assertions.assertThat(userResponse.getSurname()).isEqualTo(userDao.getSurname());
        });
        
        //then:
        verify(userRepository, times(1)).save(ArgumentMatchers.any(UserDAO.class));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2})
    public void putTest(String path) {
        //given:
        UserDTO userDtoRequest = new UserDTO(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDAO<Long> userDao = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        //when:
        when(modelMapper.map(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        when(modelMapper.map(ArgumentMatchers.any(UserBO.class), ArgumentMatchers.any(Type.class))).thenReturn(userDao);
        
        when(modelMapper.map(userBo, UserDTO.class)).thenReturn(userDtoRequest);
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        
        when(userRepository.findById(userDao.getId())).thenReturn(Mono.just(userDao));
        when(userRepository.save(ArgumentMatchers.any(UserDAO.class))).thenReturn(Mono.just(userDao));
        when(passwordEncoder.encode(userDao.getPassword())).thenReturn("encodePassword");
       
        webTestClient.put()
        .uri(path.concat("/{id}"), Collections.singletonMap("id", userDao.getId()))
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(userDtoRequest), UserDTO.class)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(UserDTO.class)
        .consumeWith(response -> {
            //then:
            UserDTO userResponse = response.getResponseBody();
            Assertions.assertThat(userResponse.getId()).isNotNull();
            Assertions.assertThat(userResponse.getId() > 0).isTrue();
            Assertions.assertThat(userResponse.getId()).isEqualTo(userDao.getId());
            Assertions.assertThat(userResponse.getBirthdate()).isEqualTo(userDao.getBirthdate());
            Assertions.assertThat(userResponse.getEmail()).isEqualTo(userDao.getEmail());
            Assertions.assertThat(userResponse.getName()).isEqualTo(userDao.getName());
            Assertions.assertThat(userResponse.getSurname()).isEqualTo(userDao.getSurname());
            Assertions.assertThat(userResponse.getPassword()).isEqualTo(userDao.getPassword());
        });
        
        //then:
        verify(userRepository, times(1)).findById(userDao.getId());
        verify(userRepository, times(1)).save(ArgumentMatchers.any(UserDAO.class));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {USER_BASE_URL_V1, USER_BASE_URL_V2})
    public void deleteTest(String path) {
        //given:
        UserDAO<Long> userDao = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDTO userDtoRequest = new UserDTO(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        //when:
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        when(modelMapper.map(ArgumentMatchers.any(UserBO.class), ArgumentMatchers.any(Type.class))).thenReturn(userDao);
        when(userRepository.findById(userDao.getId())).thenReturn(Mono.just(userDao));
        when(userRepository.delete(userDao)).thenReturn(Mono.empty());
        webTestClient.delete()
            .uri(path.concat("/{id}"), Collections.singletonMap("id", userDtoRequest.getId()))
            .exchange()
            .expectStatus().isNoContent()
            .expectBody().isEmpty();
        
        //then:
        verify(userRepository, times(1)).delete(userDao);
    }
}
