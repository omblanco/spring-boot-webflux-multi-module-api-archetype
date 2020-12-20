#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.user;

import java.lang.reflect.Type;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ${package}.model.entity.user.UserDAO;
import ${package}.model.entity.user.UserFilterDAO;
import ${package}.model.repository.user.UserRepository;
import ${package}.service.CommonReactiveServiceImpl;

import lombok.Builder;
import reactor.core.publisher.Mono;

/**
 * Implmentación del servicio de usuarios
 * @author oscar.martinezblanco
 *
 */
@Service
public class UserServiceImpl<ID> extends CommonReactiveServiceImpl<UserBO<ID>, UserDAO<ID>, UserRepository<ID>, ID> implements UserService<ID> {

    private ModelMapper modelMapper;
    
    private BCryptPasswordEncoder passwordEncoder;

    @Builder
    public UserServiceImpl(UserRepository<ID> repository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        super(repository);
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Page<UserBO<ID>>> findByFilter(UserFilterBO filter, Pageable pageable) {
        return repository.findAll(convertFilterToDao(filter), pageable)
                .map(this::convertPageToBo);
    }

    @Override
    public Mono<UserBO<ID>> save(UserBO<ID> userBo) {
        userBo.setPassword(passwordEncoder.encode(userBo.getPassword()));
        return super.save(userBo);
    }

    @Override
    public Mono<UserBO<ID>> findByEmail(String email) {
        return repository.findByEmail(email).map(this::convertToBo);
    }
    
    /**
     * Transforma un filtro bo en un filtro dao
     * @param bo Filtro de la capa de negocio
     * @return Filtro de la capa de persistencia
     */
    private UserFilterDAO convertFilterToDao(UserFilterBO bo) {
        return modelMapper.map(bo, UserFilterDAO.class);
    }

    @Override
    protected UserBO<ID> convertToBo(UserDAO<ID> dao) {
        Type userType = new TypeToken<UserBO<ID>>() {}.getType();
        UserBO<ID> result = modelMapper.map(dao, userType);
        result.setId(dao.getId());
        
        return result;
    }

    @Override
    protected Page<UserBO<ID>> convertPageToBo(Page<UserDAO<ID>> daoPage) {
        return new PageImpl<UserBO<ID>>(daoPage.getContent().stream().map(user -> {
            return this.convertToBo(user);
        }).collect(Collectors.toList()), daoPage.getPageable(), daoPage.getTotalElements());
    }

    @Override
    protected UserDAO<ID> convertToDao(UserBO<ID> bo) {
        Type userType = new TypeToken<UserDAO<ID>>() {}.getType();
        UserDAO<ID> result = modelMapper.map(bo, userType);
        result.setId(bo.getId());
        
        return result;
    }
}
