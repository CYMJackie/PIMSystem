package model.search;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import model.Record;

/** A single condition of a search query, for example {@code deadline > 2026-11-10 09:00:00}. */
public final class Criterion {

    /** Same format the create functions accept, so a search value can be copied from a stored record. */
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);

    private final Field field;
    private final Operator operator;
    private final Type typeValue;
    private final String textValue;
    private final LocalDateTime timeValue;

    private Criterion(Field field, Operator operator, Type typeValue, String textValue, LocalDateTime timeValue) {
        this.field = field;
        this.operator = operator;
        this.typeValue = typeValue;
        this.textValue = textValue;
        this.timeValue = timeValue;
    }

    public static Criterion forType(Type type) {
        return new Criterion(Field.TYPE, Operator.EQUAL, type, null, null);
    }

    public static Criterion forText(Field field, String value) {
        return new Criterion(field, Operator.CONTAINS, null, value, null);
    }

    public static Criterion forTime(Field field, Operator operator, LocalDateTime value) {
        return new Criterion(field, operator, null, null, value);
    }

    public Field getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getTextValue() {
        return textValue;
    }

    /** True for the {@code type = ...} condition, the only one that names a record type outright. */
    public boolean isTypeTest() {
        return field == Field.TYPE;
    }

    public boolean matches(Record record) {
        return switch (field.getKind()) {
            case TYPE -> Type.of(record) == typeValue;
            case TEXT -> {
                String value = field.readText(record);
                // FR-17 "contains": case-insensitive so the user does not have to remember capitalisation.
                yield value != null && value.toLowerCase().contains(textValue.toLowerCase());
            }
            case TIME -> {
                LocalDateTime value = field.readTime(record);
                if (value == null) {
                    yield false;
                }
                int comparison = value.compareTo(timeValue);
                yield switch (operator) {
                    case LESS_THAN -> comparison < 0;
                    case GREATER_THAN -> comparison > 0;
                    case EQUAL -> comparison == 0;
                    default -> false;
                };
            }
        };
    }

    /** What this one condition says about the record types it can be true for. */
    public TypeProfile profile() {
        return switch (field.getKind()) {
            case TYPE -> TypeProfile.ofType(typeValue);
            case TEXT, TIME -> TypeProfile.maybeFor(field.getRecordTypes());
        };
    }

    /** How the condition is shown back to the user. */
    public String render() {
        return switch (field.getKind()) {
            case TYPE -> field.getLabel() + " = " + typeValue.getLabel();
            // Quote marks are ordinary characters in a value, so they are never added here
            case TEXT -> field.getLabel() + " " + operator.getSymbol() + " " + textValue;
            case TIME -> field.getLabel() + " " + operator.getSymbol() + " " + timeValue.format(TIME_FORMAT);
        };
    }
}
