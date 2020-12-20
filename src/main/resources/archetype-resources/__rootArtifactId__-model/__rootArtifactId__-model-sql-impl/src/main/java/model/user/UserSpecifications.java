#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import ${package}.model.BaseSpecifications;
import ${package}.model.entity.user.UserFilterDAO;

class UserSpecifications extends BaseSpecifications {
    
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SURNAME = "surname";
    private static final String FIELD_EMAIL = "email";

    /**
     * Constructor privado
     */
    private UserSpecifications() {
        // Constructor vacio
    }
    
    public static Specification<User> withFilter(final UserFilterDAO filter) {
        return new Specification<User>() {
            
            private static final long serialVersionUID = 5298513347455447863L;

            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

                List<Predicate> predicates = new ArrayList<Predicate>() ;
                
                addLikeIgnoreCaseIfNotEmpty(filter.getName(), predicates, builder, root.get(FIELD_NAME).as(String.class));
                addLikeIgnoreCaseIfNotEmpty(filter.getSurname(), predicates, builder, root.get(FIELD_SURNAME).as(String.class));
                addLikeIgnoreCaseIfNotEmpty(filter.getEmail(), predicates, builder, root.get(FIELD_EMAIL).as(String.class));

                return builder.and(predicates.toArray(new Predicate[] {}));
            }
        };
    }
}
