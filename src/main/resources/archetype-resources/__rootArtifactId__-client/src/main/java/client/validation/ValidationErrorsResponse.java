#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.validation;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Resultado de la validación de una petición
 * @author ombla
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ValidationErrorsResponse implements Serializable {
    
    private static final long serialVersionUID = -646517201289834673L;
    
    private String code;
    
    private String message;
    
    private List<ValidationError> errors;
}