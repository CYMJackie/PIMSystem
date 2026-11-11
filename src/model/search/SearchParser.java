package model.search;

import java.util.List;

/**
 * Turns a token list into a search expression.
 *
 * Precedence is documented for FR-20: "!" binds tighter than "&&", which binds
 * tighter than "||", and operators of the same kind group to the left. Recursive
 * descent is used, so each level of the grammar is one method:
 *
 *   or      := and ( "||" and )*
 *   and     := unary ( "&&" unary )*
 *   unary   := "!" unary | primary
 *   primary := "(" or ")" | criterion
 */
public final class SearchParser {

    private final List<Token> tokens;
    private int position;

    private SearchParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static SearchExpression parse(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty())
            throw new IllegalArgumentException("The search condition is empty.");

        SearchParser parser = new SearchParser(tokens);
        SearchExpression expression = parser.parseOr();

        if (parser.position != tokens.size())
            throw new IllegalArgumentException("Unexpected input: " + tokens.get(parser.position).render());
        return expression;
    }

    private SearchExpression parseOr() {
        SearchExpression left = parseAnd();
        while (matches(TokenType.OR)) {
            int operator = position - 1;
            left = new OrExpression(left, parseAnd(), operator);
        }
        return left;
    }

    private SearchExpression parseAnd() {
        SearchExpression left = parseUnary();
        while (matches(TokenType.AND)) {
            int operator = position - 1;
            left = new AndExpression(left, parseUnary(), operator);
        }
        return left;
    }

    private SearchExpression parseUnary() {
        if (matches(TokenType.NOT))
            return new NotExpression(parseUnary(), position - 1);
        return parsePrimary();
    }

    private SearchExpression parsePrimary() {
        if (matches(TokenType.LEFT_PAREN)) {
            SearchExpression inner = parseOr();
            if (!matches(TokenType.RIGHT_PAREN))
                throw new IllegalArgumentException("A ')' is missing.");
            return inner;
        }
        if (matches(TokenType.CRITERION))
            return new LeafExpression(tokens.get(position - 1).getCriterion(), position - 1);

        throw new IllegalArgumentException("A condition was expected.");
    }

    private boolean matches(TokenType type) {
        if (position < tokens.size() && tokens.get(position).getType() == type) {
            position++;
            return true;
        }
        return false;
    }
}
