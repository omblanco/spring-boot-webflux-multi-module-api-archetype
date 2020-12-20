#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ${package}.model.entity.user.UserDAO;
import ${package}.service.CommonService;

import reactor.core.publisher.Mono;

/**
 * Interfaz del servicio de usuario
 * @author oscar.martinezblanco
 *
 */
public interface UserService<ID> extends CommonService<UserBO<ID>, UserDAO<ID>, ID>{

    /**
     * Recupera usuarios paginados y filtrados
     * @param filter Filtro de búsqueda
     * @param pageable Paginación
     * @return Mono de página de usuarios
     */
    Mono<Page<UserBO<ID>>> findByFilter(UserFilterBO filter, Pageable pageable);
    
    /**
     * Busca un usuario por email
     * @param email Email
     * @return Usuario
     */
    Mono<UserBO<ID>> findByEmail(String email);
}
