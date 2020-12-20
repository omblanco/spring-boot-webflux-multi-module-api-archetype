#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.web.errorhandling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class ValidationErrorsResponse implements Serializable {
    
    private static final long serialVersionUID = -646517201289834673L;
    
    private String code;
    
    private String message;
    
    private List<ValidationError> errors = new ArrayList<>();

    @JsonCreator
    public ValidationErrorsResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void add(String path, String code, String message) {
        this.errors.add(new ValidationError(path, code, message));
    }
    
    public void add(List<ValidationError> errors) {
        this.errors.addAll(errors);
    }
}