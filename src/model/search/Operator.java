package model.search;

/** The comparison used by a single condition (FR-17 for text, FR-18 for time). */
public enum Operator {
    CONTAINS("contains"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    EQUAL("=");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
