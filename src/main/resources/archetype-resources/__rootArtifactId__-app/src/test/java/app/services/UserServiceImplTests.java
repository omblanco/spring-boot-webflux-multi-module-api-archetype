#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.app.services;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ${package}.model.entity.user.UserDAO;
import ${package}.model.repository.user.UserRepository;
import ${package}.service.user.UserBO;
import ${package}.service.user.UserService;
import ${package}.service.user.UserServiceImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Tests unitarios del UserService
 * @author oscar.martinezblanco
 *
 */
public class UserServiceImplTests {

    @Mock
    private UserRepository<Long> mockUserRepository;
    
    @Mock
    private ModelMapper modelMapper;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    
    private UserService<Long> userService;
    
    @BeforeEach
    public void setUp() {
        openMocks(this);
        userService = new UserServiceImpl<Long>(mockUserRepository, modelMapper, passwordEncoder);
    }
    
    @Test
    public void findAllTest() {
        //Given:
        UserDAO<Long> user1 = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDAO<Long> user2 = new UserDAO<Long>(2L, "Mary", "Queen", "mary@mail.com", new Date(), "1234");
        List<UserDAO<Long>> users = Arrays.asList(user1, user2);
        
        UserBO<Long> userBo1 = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo2 = new UserBO<Long>(2L, "Mary", "Queen", "mary@mail.com", new Date(), "1234");
        
        //when:
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo1, userBo2);
        when(mockUserRepository.findAll()).thenReturn(Flux.fromIterable(users));
        Flux<UserBO<Long>> fluxUsersBo = userService.findAll();
        
        //Then:
        StepVerifier.create(fluxUsersBo.log()).expectNextCount(2).verifyComplete();
        StepVerifier.create(fluxUsersBo.log()).expectNext(userBo1).expectNext(userBo2).verifyComplete();
        StepVerifier.create(fluxUsersBo.log()).expectNext(userBo1, userBo2).verifyComplete();
    }
    
    @Test
    public void findByIdTest() {
        //Given:
        UserDAO<Long> user = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        UserBO<Long> userBo = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        //when:
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        when(mockUserRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(mockUserRepository.findById(2L)).thenReturn(Mono.empty());
        
        Mono<UserBO<Long>> monoUser = userService.findById(user.getId());
        Mono<UserBO<Long>> monoVoid = userService.findById(2L);

        //Then:
        StepVerifier.create(monoUser.log()).expectNext(userBo).verifyComplete();
        StepVerifier.create(monoVoid.log()).verifyComplete();
    }
    
    @Test
    public void saveTest() {
        //Given:
        UserBO<Long> userBo = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserDAO<Long> userDao = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        //when:
        when(mockUserRepository.save(userDao)).thenReturn(Mono.just(userDao));
        when(modelMapper.map(ArgumentMatchers.any(UserDAO.class), ArgumentMatchers.any(Type.class))).thenReturn(userBo);
        when(modelMapper.map(ArgumentMatchers.any(UserBO.class), ArgumentMatchers.any(Type.class))).thenReturn(userDao);
        
        //Then:
        Mono<UserBO<Long>> monoUser = userService.save(userBo);
        
        StepVerifier.create(monoUser.log()).expectNext(userBo).verifyComplete();
    }
    
    @Test
    public void deleteTest() {
        //Given:
        UserDAO<Long> user = new UserDAO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        UserBO<Long> userBo = new UserBO<Long>(1L, "John", "Doe", "john@mail.com", new Date(), "1234");
        
        //when:
        when(modelMapper.map(ArgumentMatchers.any(UserBO.class), ArgumentMatchers.any(Type.class))).thenReturn(user);
        when(mockUserRepository.delete(user)).thenReturn(Mono.empty());
        Mono<Void> monoUser = userService.delete(userBo);
        
        //Then:
        StepVerifier.create(monoUser.log()).verifyComplete();
    }
}
