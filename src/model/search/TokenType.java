package model.search;

/** The kind of unit a search query is built from. */
public enum TokenType {
    LEFT_PAREN,
    RIGHT_PAREN,
    AND,
    OR,
    NOT,
    CRITERION
}
