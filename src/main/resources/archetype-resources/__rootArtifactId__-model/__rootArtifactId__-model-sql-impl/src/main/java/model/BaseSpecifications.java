#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

import org.springframework.util.StringUtils;

/**
 * Clase abstracta con métodos genéricos para crear las especificaciones
 * @author ombla
 *
 */
public abstract class BaseSpecifications {

    /**
     * Añade un like que no es sensible a mayúsculas y minúsculas
     * @param fieldValue Valor del campo
     * @param predicates Pedicados
     * @param builder Builder
     * @param expression Expresión con el campo
     */
    protected static void addLikeIgnoreCaseIfNotEmpty(final String fieldValue, final List<Predicate> predicates, final CriteriaBuilder builder, Expression<String> expression) {
        if (StringUtils.hasLength(fieldValue)) {
            predicates.add(builder.like(builder.lower(expression), "%" + fieldValue.toLowerCase() + "%"));
        }
    }
}
