#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Clase de negocio de usuario
 * @author oscar.martinezblanco
 *
 * @param <ID> Tipo de dato del id
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserBO<ID> {
    
    private ID id;
    
    private String name;
    
    private String surname;
    
    private String email;
    
    private Date birthdate;
    
    private String password;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserBO other = (UserBO) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        return true;
    }
}
