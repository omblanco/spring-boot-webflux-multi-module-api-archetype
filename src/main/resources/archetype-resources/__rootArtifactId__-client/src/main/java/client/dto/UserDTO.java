#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Clase DTO que recupera la información del usuario
 * @author ombla
 *
 * @param <K> Tipo de dato del id
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO<K> {

    private K id;
    
    private String name;
    
    private String surname;
    
    private String email;
    
    private Date birthdate;
    
    private String password;

}
