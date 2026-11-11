package model.search;

import java.util.List;

/**
 * One unit of a search query. The menus only ever produce a list of these, so the
 * query is built step by step and can be shown back to the user at any moment.
 */
public final class Token {

    private final TokenType type;
    private final Criterion criterion;

    private Token(TokenType type, Criterion criterion) {
        this.type = type;
        this.criterion = criterion;
    }

    public static Token leftParen() {
        return new Token(TokenType.LEFT_PAREN, null);
    }

    public static Token rightParen() {
        return new Token(TokenType.RIGHT_PAREN, null);
    }

    public static Token and() {
        return new Token(TokenType.AND, null);
    }

    public static Token or() {
        return new Token(TokenType.OR, null);
    }

    public static Token not() {
        return new Token(TokenType.NOT, null);
    }

    public static Token criterion(Criterion criterion) {
        return new Token(TokenType.CRITERION, criterion);
    }

    public TokenType getType() {
        return type;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public String render() {
        return switch (type) {
            case LEFT_PAREN -> "(";
            case RIGHT_PAREN -> ")";
            case AND -> "&&";
            case OR -> "||";
            case NOT -> "!";
            case CRITERION -> criterion.render();
        };
    }

    /** The whole query as one line, used for the menu echo and the result header. */
    public static String renderQuery(List<Token> tokens) {
        StringBuilder builder = new StringBuilder();
        for (Token token : tokens) {
            if (builder.length() > 0)
                builder.append(' ');
            builder.append(token.render());
        }
        return builder.toString();
    }
}
