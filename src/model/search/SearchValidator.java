package model.search;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Finds a part of the query that can never be true, for example
 * {@code type = Contact && type = Task}.
 *
 * This is a warning, not a rejection: FR-22 still wants the empty result list, the
 * warning only explains why the list is empty. The check is deliberately
 * conservative - a condition is only reported when all four record types are
 * ruled out - so a satisfiable query can never be rejected.
 */
public final class SearchValidator {

    /** The first impossible part of the query, plus the tokens that should be highlighted. */
    public static final class LogicalWarning {

        private final int firstToken;
        private final int lastToken;
        private final String explanation;

        private LogicalWarning(int firstToken, int lastToken, String explanation) {
            this.firstToken = firstToken;
            this.lastToken = lastToken;
            this.explanation = explanation;
        }

        public int getFirstToken() {
            return firstToken;
        }

        public int getLastToken() {
            return lastToken;
        }

        public String getExplanation() {
            return explanation;
        }
    }

    private SearchValidator() {
    }

    /** Returns the first contradiction in the order the user typed it, or null when there is none. */
    public static LogicalWarning find(SearchExpression expression) {
        for (AndExpression and : collectAndExpressions(expression)) {
            TypeProfile left = and.getLeft().profile();
            TypeProfile right = and.getRight().profile();
            if (left.and(right).isImpossible()) {
                return new LogicalWarning(and.getOperatorToken(), and.getRight().getLastToken(),
                        explain(and.getLeft(), and.getRight()));
            }
        }
        return null;
    }

    /** Says why the two sides of an AND cannot both be true, in the words that fit the case. */
    private static String explain(SearchExpression left, SearchExpression right) {
        if (left instanceof LeafExpression leftLeaf && right instanceof LeafExpression rightLeaf
                && leftLeaf.getCriterion().isTypeTest() && rightLeaf.getCriterion().isTypeTest())
            return TypeProfile.describeConflict(left.profile(), right.profile());

        EnumSet<Type> leftTypes = left.profile().possibleTypes();
        EnumSet<Type> rightTypes = right.profile().possibleTypes();
        if (leftTypes.size() == 1 && rightTypes.size() == 1 && !leftTypes.equals(rightTypes)) {
            Type leftType = leftTypes.iterator().next();
            Type rightType = rightTypes.iterator().next();
            return "The first condition needs a " + leftType.getLabel() + " record and the second needs a "
                    + rightType.getLabel() + " record, so no single record can satisfy both.";
        }
        return "The two conditions can never be satisfied by the same kind of record.";
    }

    /**
     * Every AND, in reading order. Walking the left side first means the inner AND of
     * {@code a && b && c} is checked before the outer one, so the reported operator is
     * the one that actually made the query impossible.
     */
    private static List<AndExpression> collectAndExpressions(SearchExpression expression) {
        List<AndExpression> found = new ArrayList<>();
        collect(expression, found);
        return found;
    }

    private static void collect(SearchExpression expression, List<AndExpression> found) {
        if (expression instanceof AndExpression and) {
            collect(and.getLeft(), found);
            found.add(and);
            collect(and.getRight(), found);
        } else if (expression instanceof OrExpression or) {
            collect(or.getLeft(), found);
            collect(or.getRight(), found);
        } else if (expression instanceof NotExpression not) {
            collect(not.getChild(), found);
        }
    }
}
