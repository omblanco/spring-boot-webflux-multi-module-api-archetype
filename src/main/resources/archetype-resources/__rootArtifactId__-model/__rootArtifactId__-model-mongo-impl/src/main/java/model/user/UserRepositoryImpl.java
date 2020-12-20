#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.user;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ${package}.model.CommonRepositoryImpl;
import ${package}.model.entity.user.UserDAO;
import ${package}.model.entity.user.UserFilterDAO;
import ${package}.model.repository.user.UserRepository;

import lombok.Builder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación del repositorio extendido para recuperar los usuarios paginados y ordenados
 * @author oscar.martinezblanco
 *
 */
@Repository
public class UserRepositoryImpl extends CommonRepositoryImpl<UserDAO<String>, User, String, MongoUserRepository> implements UserRepository<String> {
    private static final String NAME_PARAM = "name";
    private static final String EMAIL_PARAM = "email";
    private static final String SURNAME_PARAM = "surname";
    
    private ModelMapper modelMapper;
    
    private ReactiveMongoTemplate mongoTemplate;
    
    @Builder
    public UserRepositoryImpl(MongoUserRepository reactiveMongoRepository, ReactiveMongoTemplate mongoTemplate,
            ModelMapper modelMapper) {
        super(reactiveMongoRepository);
        this.mongoTemplate = mongoTemplate;
        this.modelMapper = modelMapper;
    }

    /**
     * Recupera un listado ordenado, paginado y filtrado
     * @param filter Filtro
     * @param pageable Paginación
     * @return Listado
     */
    private Flux<User> findBy(UserFilterDAO filter, Pageable pageable) {
        
        List<AggregationOperation> operations = new ArrayList<AggregationOperation>();
        
        addIlikeOperation(operations, NAME_PARAM, filter.getName());
        addIlikeOperation(operations, EMAIL_PARAM, filter.getEmail());
        addIlikeOperation(operations, SURNAME_PARAM, filter.getSurname());
        addSortOperation(operations, pageable.getSort());
        addPageableOperations(operations, pageable);

        Aggregation aggregate = Aggregation.newAggregation(operations);
        
        return mongoTemplate.aggregate(aggregate, User.class, User.class);
    }
    
    /**
     * Realiza un count de los resultados de la búsqueda
     * @param filter Filtro
     * @return Número de resultados totales
     */
    private Mono<Long> countBy(UserFilterDAO filter) {
        
        Query query = new Query();
        
        addILikeCriteriaToQuery(query, NAME_PARAM, filter.getName());
        addILikeCriteriaToQuery(query, EMAIL_PARAM, filter.getEmail());
        addILikeCriteriaToQuery(query, SURNAME_PARAM, filter.getSurname());
        
        return mongoTemplate.count(query, User.class);
    }

    @Override
    public Mono<UserDAO<String>> findByEmail(String email) {
        return reactiveMongoRepository.findByEmail(email).map(this::convertToDao);
    }

    @Override
    public Mono<Page<UserDAO<String>>> findAll(UserFilterDAO filter, Pageable pageable) {
        return this.countBy(filter).flatMap(count -> {
            return this.findBy(filter, pageable).collect(Collectors.toList()).flatMap(users -> {
                return Mono.just(convertPageToDao(new PageImpl<User>(users, pageable, count)));
            });
        });
    }

    @Override
    protected UserDAO<String> convertToDao(User model) {
        UserDAO<String> result = new UserDAO<String>();
        modelMapper.map(model, result);
        result.setId(model.getId());
        
        return result;
    }

    @Override
    protected Page<UserDAO<String>> convertPageToDao(Page<User> entityPage) {
        return new PageImpl<UserDAO<String>>(entityPage.getContent().stream().map(user -> {
            return this.convertToDao(user);
        }).collect(Collectors.toList()), entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    protected User convertToModel(UserDAO<String> dao) {
        return modelMapper.map(dao, User.class);
    }

    @Override
    public Mono<Void> deleteAll() {
        return reactiveMongoRepository.deleteAll();
    }
}
