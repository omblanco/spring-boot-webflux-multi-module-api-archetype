#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.entity.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Modelo de usuario para el acceso a base de datos
 * @author ombla
 *
 * @param <ID> Tipo de dato del id
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDAO<ID> {
    
    private ID id;
    
    private String name;
    
    private String surname;
    
    private String email;
    
    private Date birthdate;
    
    private String password;
}
