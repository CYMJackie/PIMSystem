package model.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws the query with the offending part marked, the way a compiler marks the
 * line that failed, for example:
 *
 * <pre>
 *   type = Contact &amp;&amp; name contains "John" [&amp;&amp; type = Task]
 *                                                ^^^^^^^^^^^^^^
 * </pre>
 *
 * A long query is cut down to a window around the marked part, so the message
 * stays readable no matter how many conditions were entered.
 */
public final class QueryHighlighter {

    /** Queries longer than this are shown around the marked part instead of in full. */
    public static final int MAX_TOKENS = 12;

    private QueryHighlighter() {
    }

    public static List<String> highlight(List<Token> tokens, int from, int to) {
        int first = Math.max(0, from);
        int last = Math.min(tokens.size() - 1, to);

        int windowStart;
        int windowEnd;
        if (tokens.size() <= MAX_TOKENS) {
            windowStart = 0;
            windowEnd = tokens.size() - 1;
        } else if (last - first + 1 >= MAX_TOKENS) {
            windowStart = first;
            windowEnd = last;
        } else {
            int context = (MAX_TOKENS - (last - first + 1)) / 2;
            windowStart = Math.max(0, first - context);
            windowEnd = Math.min(tokens.size() - 1, last + context);
        }

        StringBuilder text = new StringBuilder();
        if (windowStart > 0)
            text.append("... ");

        int[] starts = new int[windowEnd - windowStart + 1];
        for (int i = windowStart; i <= windowEnd; i++) {
            if (i > windowStart)
                text.append(' ');
            starts[i - windowStart] = text.length();
            text.append(tokens.get(i).render());
        }
        int markStart = starts[first - windowStart];
        int markEnd = starts[last - windowStart] + tokens.get(last).render().length();

        if (windowEnd < tokens.size() - 1)
            text.append(" ...");

        String line = text.toString();
        String marked = line.substring(0, markStart)
                + "[" + line.substring(markStart, markEnd) + "]"
                + line.substring(markEnd);

        StringBuilder carets = new StringBuilder();
        carets.append(" ".repeat(marked.length()));
        // The opening '[' shifts the marked text one place to the right
        for (int i = markStart + 1; i <= markEnd; i++)
            carets.setCharAt(i, '^');

        List<String> lines = new ArrayList<>();
        lines.add("  " + marked);
        lines.add(("  " + carets).stripTrailing());
        return lines;
    }
}
