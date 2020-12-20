#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.validation;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Resultado de la validación de un campo
 * @author ombla
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidationError implements Serializable {

    private static final long serialVersionUID = 9130976257311522507L;
    
    private String path;
    
    private String code;
    
    private String message;
}