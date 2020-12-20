#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.user;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import ${package}.model.CommonRepositoryImpl;
import ${package}.model.entity.user.UserDAO;
import ${package}.model.entity.user.UserFilterDAO;
import ${package}.model.repository.user.UserRepository;

import lombok.Builder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Implementación del respositorio de usuarios
 * @author ombla
 *
 */
@Repository
class UserRepositoryImpl extends CommonRepositoryImpl<UserDAO<Long>, User, Long, JpaUserRepository> implements UserRepository<Long> {

    private ModelMapper modelMapper;
    
    @Builder
    public UserRepositoryImpl(JpaUserRepository jpaRepository, ModelMapper modelMapper) {
        super(jpaRepository);
        this.modelMapper = modelMapper;
    }
    
    @Override
    public Mono<UserDAO<Long>> findByEmail(String email) {
        return Mono.defer(() -> Mono.just(jpaRepository.findByEmail(email))).flatMap(optional -> {
            if (optional.isPresent()) {
                return Mono.just(convertToDao(optional.get()));
            }
            
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    public Mono<Page<UserDAO<Long>>> findAll(UserFilterDAO filter, Pageable pageable) {
        return Mono.defer(() -> Mono.just(jpaRepository.findAll(UserSpecifications.withFilter(filter), pageable)))
                .map(this::convertPageToDao)
                .subscribeOn(Schedulers.boundedElastic());
    }
    
    @Override
    protected UserDAO<Long> convertToDao(User model) {
        
        UserDAO<Long> result = new UserDAO<Long>();
        modelMapper.map(model, result);
        result.setId(model.getId());
        
        return result;
    }

    @Override
    protected Page<UserDAO<Long>> convertPageToDao(Page<User> entityPage) {
        return new PageImpl<UserDAO<Long>>(entityPage.getContent().stream().map(user -> {
            return this.convertToDao(user);
        }).collect(Collectors.toList()), entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    protected User convertToModel(UserDAO<Long> dao) {
        return modelMapper.map(dao, User.class);
    }

    @Override
    public Mono<Void> deleteAll() {
        return Mono.defer(() -> {
            jpaRepository.deleteAll();
            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
