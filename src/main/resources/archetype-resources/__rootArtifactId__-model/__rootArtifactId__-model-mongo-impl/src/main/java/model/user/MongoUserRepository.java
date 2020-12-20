#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.user;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;


public interface MongoUserRepository extends ReactiveMongoRepository<User, String> {
    
    /**
     * Busca un usuario por email
     * @param email email
     * @return Usuario
     */
    Mono<User> findByEmail(String email);
}
