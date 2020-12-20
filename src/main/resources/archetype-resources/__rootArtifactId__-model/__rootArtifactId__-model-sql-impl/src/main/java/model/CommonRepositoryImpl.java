#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import ${package}.model.repository.CommonRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@AllArgsConstructor
public abstract class CommonRepositoryImpl<DAO, M, ID, R extends JpaRepository<M, ID>> implements CommonRepository<DAO, ID> {

    protected R jpaRepository;
    
    @Override
    public Flux<DAO> findAll() {
        return Flux.defer(() -> Flux.fromIterable(jpaRepository.findAll()
                .stream().map(this::convertToDao)
                .collect(Collectors.toList())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<DAO> findById(ID id) {
        return Mono.defer(() -> Mono.just(jpaRepository.findById(id))).flatMap(optional -> {
            if (optional.isPresent()) {
                return Mono.just(convertToDao(optional.get()));
            }
            
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<DAO> save(DAO dao) {
        return Mono.defer(() -> Mono.just(convertToDao(jpaRepository.save(convertToModel(dao)))))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(DAO dao) {
        return Mono.defer(() -> {
            jpaRepository.delete(convertToModel(dao));
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
    
    
    /**
     * Conversión de modelo a dao
     * @param model Modelo
     * @return dao
     */
    protected abstract DAO convertToDao(M model);
    
    /**
     * Transforma una página de modelo a página de daos
     * @param entityPage Página de modelos
     * @return Página de daos
     */
    protected abstract Page<DAO> convertPageToDao(Page<M> entityPage);
    
    /**
     * Transforma un DAO a una entidad model
     * @param dao Dao 
     * @return Modelo
     */
    protected abstract M convertToModel(DAO dao);
}
