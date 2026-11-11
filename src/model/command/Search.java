package model.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.PIRManager;
import model.Record;
import model.search.Criterion;
import model.search.Field;
import model.search.QueryHighlighter;
import model.search.SearchExpression;
import model.search.SearchParser;
import model.search.SearchResult;
import model.search.SearchValidator;
import model.search.Token;

/**
 * The search function (FR-16 to FR-23).
 *
 * The query is already a list of tokens when it reaches here, because the menus
 * build it one choice at a time. This class only parses, evaluates and collects
 * the display information, so it can be unit tested without any user input.
 */
public class Search {

    public SearchResult search(PIRManager manager, List<Token> tokens) {
        SearchExpression expression = SearchParser.parse(tokens);

        // FR-23: work on the records as they are, never on a modified copy of the collection.
        List<Record> all = manager.getRecords();
        Set<Integer> matchingIds = expression.evaluate(all);

        List<Record> matches = new ArrayList<>();
        for (Record record : all)
            if (matchingIds.contains(record.getId()))
                matches.add(record);
        matches.sort(Comparator.comparingInt(Record::getId));

        List<Criterion> criteria = new ArrayList<>();
        expression.collectCriteria(criteria);

        List<Field> fields = new ArrayList<>();
        Map<Field, List<String>> terms = new LinkedHashMap<>();
        for (Criterion criterion : criteria) {
            Field field = criterion.getField();
            if (field == Field.TYPE)
                continue;
            if (!fields.contains(field))
                fields.add(field);
            if (criterion.getTextValue() != null)
                terms.computeIfAbsent(field, key -> new ArrayList<>()).add(criterion.getTextValue());
        }

        return new SearchResult(Token.renderQuery(tokens), matches, fields, terms, buildWarning(tokens, expression));
    }

    /** Explains an impossible condition instead of only returning nothing (a warning, not an error). */
    private String buildWarning(List<Token> tokens, SearchExpression expression) {
        SearchValidator.LogicalWarning warning = SearchValidator.find(expression);
        if (warning == null)
            return null;

        StringBuilder block = new StringBuilder();
        block.append("Logical warning: the highlighted conditions can never be true at the same time.");
        block.append('\n');
        for (String line : QueryHighlighter.highlight(tokens, warning.getFirstToken(), warning.getLastToken()))
            block.append(line).append('\n');
        block.append("  ").append(warning.getExplanation());
        return block.toString();
    }
}
