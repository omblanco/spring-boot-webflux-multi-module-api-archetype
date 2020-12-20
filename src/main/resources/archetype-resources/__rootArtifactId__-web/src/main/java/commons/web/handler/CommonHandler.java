#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.commons.web.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import ${package}.commons.web.errorhandling.ValidationError;
import ${package}.commons.web.errorhandling.ValidationErrorsResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Clase base para los handlers
 * @author oscar.martinezblanco
 *
 */
public abstract class CommonHandler {

    protected static final String PAGE_PARAM_NAME = "page";
    protected static final String SIZE_PARAM_NAME = "size";
    protected static final String SORT_PARAM_NAME = "sort";
    protected static final String SPLIT_SORT_TOKEN = ",";
    protected static final String ORDER_DESC_NAME = "desc";
    
    /**
     * Genera un flux con una respuesta de validación de entidades
     * @param errors errores
     * @param message Mensaje
     * @return Mono
     */
    protected Mono<ServerResponse> validationErrorsResponse(Errors errors, String message) {
        
        return Flux.fromIterable(errors.getFieldErrors())
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getCode(), fieldError.getDefaultMessage()))
                .collectList().flatMap(list -> {
                    var errorsResponse = new ValidationErrorsResponse(HttpStatus.UNPROCESSABLE_ENTITY.name(), message);
                    errorsResponse.add(list);
                    return ServerResponse.unprocessableEntity().body(fromValue(errorsResponse));
                });
    }
    
    /**
     * Extrae el objeto de paginación de la request
     * @param request Request
     * @return Paginación
     */
    protected Pageable getPageableFromRequest(ServerRequest request) {
        Integer page = Integer.parseInt(request.queryParam(PAGE_PARAM_NAME).get());
        Integer size = Integer.parseInt(request.queryParam(SIZE_PARAM_NAME).get());
        
        List<String> sortList = request.queryParams().get(SORT_PARAM_NAME);
        List<Order> orders = new ArrayList<>();
        if (sortList != null ) {
            for (String sort : sortList) {
                if (StringUtils.hasLength(sort)) {
                    String[] sortTokens = sort.split(SPLIT_SORT_TOKEN);
                    
                    Direction direction = Direction.ASC;
                    if (sortTokens.length == 2 && sortTokens[1].equalsIgnoreCase(ORDER_DESC_NAME)) {
                        direction = Direction.DESC;
                    }
                    Order order = new Order(direction, sortTokens[0]);
                    orders.add(order);
                }
            }
        }
        
        return PageRequest.of(page, size,  Sort.by(orders));
    }
    
    /**
     * Valida que un parámetro exista en la request y no esté vacío
     * @param request Request
     * @param paramName Nombre del parámetro
     * @return resultado de la operación
     */
    protected boolean validateIsPresentAndNotEmptyParam(ServerRequest request, String paramName) {
        return request.queryParam(paramName).isPresent() && StringUtils.hasLength(request.queryParam(paramName).get()); 
    }
    
    /**
     * Valida que el parámetro está presente y devuelve su valor
     * @param request Rquest
     * @param paramName nombre del parámetro
     * @return Valor del parámetro
     */
    protected String getParamValue(ServerRequest request, String paramName) {
        
        if (request.queryParam(paramName).isPresent()) {
            return request.queryParam(paramName).get();
        }
        
        return null;
    }
}
