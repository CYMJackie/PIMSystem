package model.search;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import model.Record;

/** A single condition, for example {@code type = Task}. */
public final class LeafExpression extends SearchExpression {

    private final Criterion criterion;
    private final int tokenIndex;

    public LeafExpression(Criterion criterion, int tokenIndex) {
        this.criterion = criterion;
        this.tokenIndex = tokenIndex;
    }

    @Override
    public Set<Integer> evaluate(List<Record> records) {
        Set<Integer> ids = new LinkedHashSet<>();
        for (Record record : records)
            if (criterion.matches(record))
                ids.add(record.getId());
        return ids;
    }

    @Override
    public TypeProfile profile() {
        return criterion.profile();
    }

    public Criterion getCriterion() {
        return criterion;
    }

    @Override
    public void collectCriteria(List<Criterion> criteria) {
        criteria.add(criterion);
    }

    @Override
    public int getFirstToken() {
        return tokenIndex;
    }

    @Override
    public int getLastToken() {
        return tokenIndex;
    }
}
