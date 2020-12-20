#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import org.springframework.data.domain.Page;

import ${package}.commons.annotation.loggable.Loggable;
import ${package}.commons.annotation.traceable.Traceable;
import ${package}.model.repository.CommonRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación abstracta del servicio genérico
 * para repositorios reactivos de mongo
 * @author oscar.martinezblanco
 *
 * @param <BO> Clase de negocio
 * @param <DAO> Clase de modelo para el acceso a datos
 * @param <R> Repositorio
 * @param <ID> Tipo de dato del id
 */
@Traceable
@Loggable
@AllArgsConstructor
public abstract class CommonReactiveServiceImpl <BO, DAO, R extends CommonRepository<DAO, ID>, ID> implements CommonService<BO, DAO, ID>{

    protected R repository;
    
    @Override
    public Flux<BO> findAll() {
        
        return repository.
                findAll()
                .map(this::convertToBo);
    }

    @Override
    public Mono<BO> findById(ID id) {
        return repository.findById(id).map(this::convertToBo);
    }

    @Override
    public Mono<BO> save(BO bo) {
        return repository.save(convertToDao(bo)).map(this::convertToBo);
    }

    @Override
    public Mono<Void> delete(BO bo) {
        return repository.delete(convertToDao(bo)).flatMap(result -> Mono.empty());
    }

    /**
     * Conversión de DAO a BO
     * @param dao DAO
     * @return bo
     */
    protected abstract BO convertToBo(DAO dao);
    
    /**
     * Transforma una página de DAOs en BOs
     * @param daoPage Página de DAOs
     * @return Página de BOs
     */
    protected abstract Page<BO> convertPageToBo(Page<DAO> daoPage);
    
    /**
     * Transforma un BO en DAO
     * @param bo BO
     * @return DAO
     */
    protected abstract DAO convertToDao(BO bo);
}
