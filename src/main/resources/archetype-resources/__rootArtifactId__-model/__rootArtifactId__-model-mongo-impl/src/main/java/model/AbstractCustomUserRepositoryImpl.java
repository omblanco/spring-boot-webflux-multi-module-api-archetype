#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

/**
 * Repositorio abstracto con métodos de utilización común
 * @author oscar.martinezblanco
 *
 */
public abstract class AbstractCustomUserRepositoryImpl {

    private static final String I_TOKEN = "i";
    
    protected void addIlikeOperation(List<AggregationOperation> operations, String field, String value) {
        if (StringUtils.hasLength(value)) {
            Criteria regex = Criteria.where(field).regex(value, I_TOKEN);
            MatchOperation match = new MatchOperation(regex);
            operations.add(match);
        }
    }
    
    protected void addSortOperation(List<AggregationOperation> operations, Sort sort) {
        if (sort != null && !sort.isEmpty()) {
            SortOperation sortOpertions = new SortOperation(sort);
            operations.add(sortOpertions);
        }
    }

    protected void addPageableOperations(List<AggregationOperation> operations, Pageable pageable) {
        SkipOperation skipOpertions = new SkipOperation(pageable.getPageNumber() * pageable.getPageSize());
        operations.add(skipOpertions);
        
        LimitOperation limitOperations = new LimitOperation(pageable.getPageSize());
        operations.add(limitOperations);
    }
    
    protected void addILikeCriteriaToQuery(Query query, String field, String value) {
        if (StringUtils.hasLength(value)) {
            Criteria regex = Criteria.where(field).regex(value, I_TOKEN);
            query.addCriteria(regex);
        }
    }
}
