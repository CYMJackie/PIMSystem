package model.search;

import java.util.List;
import java.util.Set;

import model.Record;

/**
 * A parsed search query. The parser builds these from the token list, then the
 * expression evaluates itself and can also report what it implies about record types.
 */
public abstract class SearchExpression {

    /** FR-21: the identifiers of every record that satisfies this expression. */
    public abstract Set<Integer> evaluate(List<Record> records);

    /** Which record types this expression can possibly be true for. */
    public abstract TypeProfile profile();

    /** Appends every condition in the query, in the order the user wrote them. */
    public abstract void collectCriteria(List<Criterion> criteria);

    /** Index of the first token of this expression inside the token list. */
    public abstract int getFirstToken();

    /** Index of the last token of this expression inside the token list. */
    public abstract int getLastToken();
}
