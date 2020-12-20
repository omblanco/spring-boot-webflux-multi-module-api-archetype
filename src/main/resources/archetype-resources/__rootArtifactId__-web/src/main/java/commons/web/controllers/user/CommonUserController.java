#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.web.controllers.user;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ${package}.commons.annotation.loggable.Loggable;
import ${package}.commons.web.controllers.CommonController;
import ${package}.commons.web.dto.CommonDTO;
import ${package}.commons.web.dto.user.UserFilterDTO;
import ${package}.model.entity.user.UserDAO;
import ${package}.service.user.UserBO;
import ${package}.service.user.UserFilterBO;
import ${package}.service.user.UserService;

import reactor.core.CorePublisher;
import reactor.core.publisher.Mono;

@Loggable
public abstract class CommonUserController<DTO extends CommonDTO<ID>, ID> extends CommonController<DTO, UserBO<ID>, UserDAO<ID>, UserService<ID>, ID> {

    protected ModelMapper modelMapper;
    
    private Class<DTO> dtoClass;
    
    @SuppressWarnings("unchecked")
    public CommonUserController(UserService<ID> service, ModelMapper modelMapper) {
        super(service);
        this.modelMapper = modelMapper;
        this.dtoClass = (Class<DTO>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    /**
     * Método que recupera Usuarios paginados
     * @param filter Filtro de búsqueda
     * @param pageable Paginación y ordenación
     * @return Página de usuarios
     */
    @GetMapping
    @ResponseBody
    public Mono<ResponseEntity<CorePublisher<?>>> findByFilter(UserFilterDTO filter,
            @SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable, Long size, Long page) {
        
        if (size == null & page == null) {
            return super.findAll();
        }
        
        return Mono.just(ResponseEntity.ok().contentType(APPLICATION_JSON)
                .body(service.findByFilter(convertFilterToDao(filter), pageable).map(this::convertPageToBo)));
    }
    
    /**
     * Transforma un filtro dto en un filtro bo
     * @param dto Filtro de la capa de vista
     * @return Filtro de la capa de negocio
     */
    protected UserFilterBO convertFilterToDao(UserFilterDTO dto) {
        return modelMapper.map(dto, UserFilterBO.class);
    }
    
    @Override
    protected DTO convertToDto(UserBO<ID> bo) {
        return modelMapper.map(bo, dtoClass);
    }
    
    @Override
    protected UserBO<ID> convertToBo(DTO dto) {
        
        Type userType = new TypeToken<UserBO<ID>>() {}.getType();
        UserBO<ID> result = modelMapper.map(dto, userType);
        result.setId(dto.getId());
        
        return result;
    }
    
    @Override
    protected Page<DTO> convertPageToBo(Page<UserBO<ID>> boPage) {
        return new PageImpl<DTO>(boPage.getContent().stream().map(user -> {
            return this.convertToDto(user);
        }).collect(Collectors.toList()), boPage.getPageable(), boPage.getTotalElements());
    }
}
