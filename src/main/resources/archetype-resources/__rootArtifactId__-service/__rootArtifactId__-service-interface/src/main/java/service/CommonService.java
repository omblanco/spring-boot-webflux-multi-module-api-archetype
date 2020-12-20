#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz del servicio genérico
 * @author oscar.martinezblanco
 *
 * @param <D> Clase DTO
 * @param <E> Clase Entity
 * @param <K> Clase del ID
 */
/**
 * Interfaz del servicio genérico
 * @author oscar.martinezblanco
 *
 * @param <BO> Clase de servicio
 * @param <DAO> Clase de modelo de datos
 * @param <ID> Clase del id de la entidad
 */
public interface CommonService<BO, DAO, ID> {

    /**
     * Recupera un listado con todos los BO
     * @return Flux de BOs
     */
    Flux<BO> findAll();
    
    /**
     * Busca un BO por la clave
     * @param id Clave
     * @return BO
     */
    Mono<BO> findById(ID id);
    
    /**
     * Guarda un BO
     * @param bo BO
     * @return BO resultado de la operación
     */
    Mono<BO> save(BO bo);
    
    /**
     * Elimina un BO
     * @param bo BO a eliminar
     * @return Resultado de la operación
     */
    Mono<Void> delete(BO bo);
}
