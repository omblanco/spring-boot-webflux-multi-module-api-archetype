#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app.web.controllers;

import static ${package}.commons.web.utils.BaseApiConstants.USER_BASE_URL_V1;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ${package}.commons.web.controllers.user.CommonUserController;
import ${package}.mongo.app.web.dtos.UserDTO;
import ${package}.service.user.UserBO;
import ${package}.service.user.UserService;

import lombok.Builder;

/**
 * Controlador para los usuarios
 * Puede contener métodos para la carga de vistas html
 * 
 * @author oscar.martinezblanco
 *
 */
@Controller
@RequestMapping(USER_BASE_URL_V1)
public class UserController extends CommonUserController<UserDTO, String> {
    
    @Builder
    public UserController(UserService<String> service, ModelMapper modelMapper) {
        super(service, modelMapper);
    }
    
    @Override
    protected String getBaseUrl() {
        return USER_BASE_URL_V1;
    }
    
    @Override
    protected void updateBoToSave(UserDTO requestDto, UserBO<String> bo) {
        bo.setBirthdate(requestDto.getBirthdate());
        bo.setEmail(requestDto.getEmail());
        bo.setName(requestDto.getName());
        bo.setSurname(requestDto.getSurname());
    }
}
