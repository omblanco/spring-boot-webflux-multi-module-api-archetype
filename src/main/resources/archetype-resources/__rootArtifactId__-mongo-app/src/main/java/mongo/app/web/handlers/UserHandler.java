#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app.web.handlers;

import static ${package}.commons.web.utils.BaseApiConstants.FORWARD_SLASH;
import static ${package}.commons.web.utils.BaseApiConstants.ID_PARAM_NAME;
import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V3;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ${package}.commons.annotation.loggable.Loggable;
import ${package}.commons.web.dto.user.UserFilterDTO;
import ${package}.commons.web.handler.CommonHandler;
import ${package}.mongo.app.web.dtos.UserDTO;
import ${package}.service.user.UserBO;
import ${package}.service.user.UserFilterBO;
import ${package}.service.user.UserService;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Alternativa al UserController y UserRestController 
 * con functional endpoints de Spring WebFlux
 * @author oscar.martinezblanco
 *
 */
@Loggable
@AllArgsConstructor
@Component
public class UserHandler extends CommonHandler {

    protected static final String NAME_PARAM_NAME = "name";
    protected static final String SURNAME_PARAM_NAME = "surname";
    protected static final String EMAIL_PARAM_NAME = "email";
    
    private static final String VALIDATION_MESSAGE = "Validation failure: userDTO";
    
    private UserService<String> userService;
    
    private Validator validator;
    
    private ModelMapper modelMapper;
    
    public Mono<ServerResponse> findAll(ServerRequest request) {
        
        Boolean validSize = validateIsPresentAndNotEmptyParam(request, SIZE_PARAM_NAME);
        Boolean validPage = validateIsPresentAndNotEmptyParam(request, PAGE_PARAM_NAME);
        
        if (validSize && validPage) {
            UserFilterDTO filter = new UserFilterDTO();
            filter.setName(getParamValue(request, NAME_PARAM_NAME));
            filter.setSurname(getParamValue(request, SURNAME_PARAM_NAME));
            filter.setEmail(getParamValue(request, EMAIL_PARAM_NAME));
            
            Pageable pageable = getPageableFromRequest(request);
            
            return ServerResponse.ok()
                    .contentType(APPLICATION_JSON)
                    .body(userService.findByFilter(convertFilterToDao(filter), pageable)
                            .map(this::convertPageToBo), Page.class);
        } else {
            return ServerResponse.ok()
                    .contentType(APPLICATION_JSON)
                        .body(userService.findAll()
                                .map(this::convertToDto)
                                .collect(Collectors.toList()), UserDTO.class);
        }
    }
    
    @Deprecated
    public Mono<ServerResponse> findByFilter(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(userService.findAll(), UserDTO.class);
    }
    
    public Mono<ServerResponse> get(ServerRequest request) {
        String id = request.pathVariable(ID_PARAM_NAME);
        
        return userService.findById(id)
                .map(this::convertToDto)
                .flatMap(userDto -> ServerResponse.ok().contentType(APPLICATION_JSON)
                .body(fromValue(userDto)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    
    public Mono<ServerResponse> create(ServerRequest request){
        Mono<UserDTO> user = request.bodyToMono(UserDTO.class);
        
        return user.flatMap(userDto -> {
            Errors errors = new BeanPropertyBindingResult(userDto, UserDTO.class.getName());
            
            validator.validate(userDto, errors);
            
            if(errors.hasErrors()) {
                return validationErrorsResponse(errors, VALIDATION_MESSAGE);
            } else {
                userDto.setId(null);
                return userService.save(convertToBo(userDto))
                        .map(this::convertToDto)
                        .flatMap(savedUserDto -> 
                        ServerResponse.created(URI.create(USER_BASE_URL_V3.concat(FORWARD_SLASH).concat(savedUserDto.getId().toString())))
                            .contentType(APPLICATION_JSON)
                            .body(fromValue(savedUserDto)));
            }
        });
    }
    
    public Mono<ServerResponse> update(ServerRequest request) {
        
        Mono<UserDTO> userMonoDto = request.bodyToMono(UserDTO.class);
        String id = request.pathVariable(ID_PARAM_NAME);
        
        return userMonoDto.flatMap(userDto -> {
            Errors errors = new BeanPropertyBindingResult(userDto, UserDTO.class.getName());
            validator.validate(userDto, errors);
            if(errors.hasErrors()) {
                return validationErrorsResponse(errors, VALIDATION_MESSAGE);
            } else {
                return userService.findById(id).map(userBo -> {
                    
                    userBo.setName(userDto.getName());
                    userBo.setSurname(userDto.getSurname());
                    userBo.setEmail(userDto.getEmail());
                    userBo.setBirthdate(userDto.getBirthdate());
                    
                    return userBo; 
                })
                .flatMap(userBo -> userService.save(userBo)
                        .map(this::convertToDto)
                        .flatMap(updatedUserDto -> 
                            ServerResponse.created(URI.create(USER_BASE_URL_V3.concat(FORWARD_SLASH).concat(updatedUserDto.getId().toString())))
                                .contentType(APPLICATION_JSON)
                                .body(fromValue(updatedUserDto))));
            }
        });
    }
    
    public Mono<ServerResponse> delete(ServerRequest request) {
        
        String id = request.pathVariable(ID_PARAM_NAME);
        
        return userService.findById(id)
                .flatMap(userBo -> userService.delete(userBo)
                .then(ServerResponse.noContent().build())
                .switchIfEmpty(ServerResponse.notFound().build()));
    }
    
    /**
     * Transforma un filtro dto en un filtro bo
     * @param dto Filtro de la capa de vista
     * @return Filtro de la capa de negocio
     */
    private UserFilterBO convertFilterToDao(UserFilterDTO dto) {
        return modelMapper.map(dto, UserFilterBO.class);
    }
    
    protected UserDTO convertToDto(UserBO<String> bo) {
        return modelMapper.map(bo, UserDTO.class);
    }

    protected Page<UserDTO> convertPageToBo(Page<UserBO<String>> boPage) {
        return new PageImpl<UserDTO>(boPage.getContent().stream().map(user -> {
            return this.convertToDto(user);
        }).collect(Collectors.toList()), boPage.getPageable(), boPage.getTotalElements());
    }

    protected UserBO<String> convertToBo(UserDTO dto) {
        UserBO<String> result = new UserBO<String>();
        modelMapper.map(dto, result);
        result.setId(dto.getId());
        
        return result;
    }
}

