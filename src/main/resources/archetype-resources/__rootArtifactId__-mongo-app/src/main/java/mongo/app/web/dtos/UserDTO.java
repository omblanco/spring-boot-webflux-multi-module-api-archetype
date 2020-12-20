#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.mongo.app.web.dtos;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import ${package}.commons.web.dto.CommonDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Clase de un usuario de la aplicación para la capa web
 * @author oscar.martinezblanco
 *
 */
@Getter
@Setter
@ToString
public class UserDTO extends CommonDTO<String> {
    
    @NotEmpty
    @Size(min = 3, max = 25)
    private String name;
    
    @NotEmpty
    @Size(min = 3, max = 50)
    private String surname;
    
    @NotEmpty
    @Size(min = 3, max = 50)
    @Email
    private String email;
    
    @NotNull
    @Past
    private Date birthdate;
    
    @NotEmpty
    @Size(min = 4, max = 25)
    private String password;
    
    @Builder
    public UserDTO(String id, String name, String surname, String email, Date birthdate, String password) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthdate = birthdate;
        this.password = password;
    }

    public UserDTO() {
        super(null);
    }
}
