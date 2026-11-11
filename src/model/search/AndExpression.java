package model.search;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import model.Record;

/** FR-19 "&&": only the records matched by both sides. */
public final class AndExpression extends SearchExpression {

    private final SearchExpression left;
    private final SearchExpression right;
    private final int operatorTokenIndex;

    public AndExpression(SearchExpression left, SearchExpression right, int operatorTokenIndex) {
        this.left = left;
        this.right = right;
        this.operatorTokenIndex = operatorTokenIndex;
    }

    public SearchExpression getLeft() {
        return left;
    }

    public SearchExpression getRight() {
        return right;
    }

    public int getOperatorToken() {
        return operatorTokenIndex;
    }

    @Override
    public Set<Integer> evaluate(List<Record> records) {
        Set<Integer> ids = new LinkedHashSet<>(left.evaluate(records));
        ids.retainAll(right.evaluate(records));
        return ids;
    }

    @Override
    public TypeProfile profile() {
        return left.profile().and(right.profile());
    }

    @Override
    public void collectCriteria(List<Criterion> criteria) {
        left.collectCriteria(criteria);
        right.collectCriteria(criteria);
    }

    @Override
    public int getFirstToken() {
        return left.getFirstToken();
    }

    @Override
    public int getLastToken() {
        return right.getLastToken();
    }
}
