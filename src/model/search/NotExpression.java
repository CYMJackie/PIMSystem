package model.search;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import model.Record;

/** FR-19 "!": every record that the covered expression does not match. */
public final class NotExpression extends SearchExpression {

    private final SearchExpression child;
    private final int notTokenIndex;

    public NotExpression(SearchExpression child, int notTokenIndex) {
        this.child = child;
        this.notTokenIndex = notTokenIndex;
    }

    public SearchExpression getChild() {
        return child;
    }

    @Override
    public Set<Integer> evaluate(List<Record> records) {
        Set<Integer> covered = child.evaluate(records);
        Set<Integer> ids = new LinkedHashSet<>();
        for (Record record : records)
            if (!covered.contains(record.getId()))
                ids.add(record.getId());
        return ids;
    }

    @Override
    public TypeProfile profile() {
        return child.profile().not();
    }

    @Override
    public void collectCriteria(List<Criterion> criteria) {
        child.collectCriteria(criteria);
    }

    @Override
    public int getFirstToken() {
        return notTokenIndex;
    }

    @Override
    public int getLastToken() {
        return child.getLastToken();
    }
}
