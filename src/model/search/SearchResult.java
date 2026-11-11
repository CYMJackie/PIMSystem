package model.search;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import model.Record;

/**
 * Everything the view needs to show one search: the query, the matching records,
 * the fields the query mentioned, and the optional logical warning.
 * The model never prints anything itself (NFR-03), the view only formats these values.
 */
public final class SearchResult {

    private final String query;
    private final List<Record> records;
    private final List<Field> fields;
    private final Map<Field, List<String>> terms;
    private final String warning;

    public SearchResult(String query, List<Record> records, List<Field> fields,
                        Map<Field, List<String>> terms, String warning) {
        this.query = query;
        this.records = List.copyOf(records);
        this.fields = List.copyOf(fields);
        this.terms = Collections.unmodifiableMap(terms);
        this.warning = warning;
    }

    public String getQuery() {
        return query;
    }

    /** FR-21 / FR-23: a copy of the matching records, sorted by identifier, never the manager's own list. */
    public List<Record> getRecords() {
        return records;
    }

    /** The fields the query referred to, in the order they were entered. */
    public List<Field> getFields() {
        return fields;
    }

    /** The text the user searched for in one field, used to pick the words shown around the match. */
    public List<String> getTerms(Field field) {
        return terms.getOrDefault(field, List.of());
    }

    public boolean hasWarning() {
        return warning != null;
    }

    public String getWarning() {
        return warning;
    }
}
