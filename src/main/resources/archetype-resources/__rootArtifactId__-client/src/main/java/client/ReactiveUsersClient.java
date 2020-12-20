#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ${package}.client.dto.RestResponsePage;
import ${package}.client.dto.UserDTO;
import ${package}.client.dto.UserFilterDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz del cliente reactivo para consumir la api
 * @author oscar.martinezblanco
 *
 */
public interface ReactiveUsersClient<K> {

    /**
     * Recupera todos los usuarios
     * @return Flux de usuarios
     */
    Flux<UserDTO<K>> getAllUsers();
    
    /**
     * Recuera un usuario por id
     * @param id Id
     * @return Usuario
     */
    Mono<UserDTO<K>> get(K id);
    
    /**
     * Crea un nuevo usuario
     * @param user Usuario
     * @return Usuario creado
     */
    Mono<UserDTO<K>> save(UserDTO<K> user);
    
    /**
     * Actualiza un usuario
     * @param user Usuario
     * @param id Id
     * @return Usuario actualizado
     */
    Mono<UserDTO<K>> update(UserDTO<K> user, K id);
    
    /**
     * Elimina un usuario
     * @param id Id de usuario a eliminar
     * @return Resultado de la operación
     */
    Mono<Void> delete(K id);
    
    /**
     * Recupera un listado de usuarios fitrado y paginado
     * @param filter Filtro de búsqueda
     * @param sort Ordenación
     * @return Lista de usuarios
     */
    Flux<UserDTO<K>> getUsers(UserFilterDTO filter, Sort sort);
    
    /**
     * Recupera un listado de Usuarios paginado, ordenado y filtrado
     * @param filter Filtro de búsqueda
     * @param pageable Paginación
     * @return Página
     */
    Mono<RestResponsePage<UserDTO<K>>> getUsers(UserFilterDTO filter, Pageable pageable);
}
