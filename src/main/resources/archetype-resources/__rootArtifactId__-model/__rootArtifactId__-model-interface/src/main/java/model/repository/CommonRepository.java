#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Interfaz genérica para los repositorios
 * @author ombla
 *
 * @param <DAO> Clase DAO de acceso a datos
 * @param <ID> Tipo de dato del ID de la entidad
 */
public interface CommonRepository<DAO, ID> {

    /**
     * Recupera un listado con todos los daos
     * @return Flux con los daos
     */
    Flux<DAO> findAll();
    
    /**
     * Busca un dao por su id
     * @param id Id del dao
     * @return Resultado de la operación
     */
    Mono<DAO> findById(ID id);
    
    /**
     * Guarda un dao
     * @param dao Dao a guardar
     * @return Resultado de la operación
     */
    Mono<DAO> save(DAO dao);
    
    /**
     * Elimina un dao
     * @param dao Dao a eliminar
     * @return Resultado de la operación
     */
    Mono<Void> delete(DAO dao);
}