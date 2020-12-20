#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.util.StringUtils;

import ${package}.model.repository.CommonRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del respositorio común para la implementación reactiva de mongo
 * @author oscar.martinezblanco
 *
 * @param <DAO> Clase DAO
 * @param <M> Clase Documento del modelo de mongo
 * @param <ID> Tipo de dato del id de la entidad
 * @param <R> Tipo de repositorio
 */
@AllArgsConstructor
public abstract class CommonRepositoryImpl<DAO, M, ID, R extends ReactiveMongoRepository<M, ID>> implements CommonRepository<DAO, ID> {

    private static final String I_TOKEN = "i";

    protected R reactiveMongoRepository;
    
    @Override
    public Flux<DAO> findAll() {
        return reactiveMongoRepository.findAll()
                .map(this::convertToDao);
    }

    @Override
    public Mono<DAO> findById(ID id) {
        return reactiveMongoRepository.findById(id)
                .map(this::convertToDao);
    }

    @Override
    public Mono<DAO> save(DAO dao) {
        return reactiveMongoRepository.save(convertToModel(dao))
                .map(this::convertToDao);
    }

    @Override
    public Mono<Void> delete(DAO dao) {
        return reactiveMongoRepository.delete(convertToModel(dao));
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
    
    protected void addIlikeOperation(List<AggregationOperation> operations, String field, String value) {
        if (StringUtils.hasLength(value)) {
            Criteria regex = Criteria.where(field).regex(value, I_TOKEN);
            MatchOperation match = new MatchOperation(regex);
            operations.add(match);
        }
    }
    
    protected void addSortOperation(List<AggregationOperation> operations, Sort sort) {
        if (sort != null && !sort.isEmpty()) {
            SortOperation sortOpertions = new SortOperation(sort);
            operations.add(sortOpertions);
        }
    }

    protected void addPageableOperations(List<AggregationOperation> operations, Pageable pageable) {
        SkipOperation skipOpertions = new SkipOperation(pageable.getPageNumber() * pageable.getPageSize());
        operations.add(skipOpertions);
        
        LimitOperation limitOperations = new LimitOperation(pageable.getPageSize());
        operations.add(limitOperations);
    }
    
    protected void addILikeCriteriaToQuery(Query query, String field, String value) {
        if (StringUtils.hasLength(value)) {
            Criteria regex = Criteria.where(field).regex(value, I_TOKEN);
            query.addCriteria(regex);
        }
    }
}
