package ie.httpeasy.utils;

public class HtmlFormatter {
    private static int indentSize = 1;
    public static String formatText(String text) {
        boolean inScriptOrStyle = false;
        StringBuilder builder = new StringBuilder(text.length());
        boolean addToCurr = false;
        StringBuilder curr = new StringBuilder(1024);
        int depth = 0;
        boolean firstTag = false;
        char c;

        for (int i = 0; i < text.length(); ++i) {
            c = text.charAt(i);
            if (c == '<') {
                firstTag = true;
                if (inScriptOrStyle) {
                    if (text.charAt(i + 1) == '/') {
                        addToCurr = true;
                        inScriptOrStyle = false;
                    }
                } else {
                    addToCurr = true;
                }
            }
            if (addToCurr) {
                curr.append(c);
            } else {
                if (!firstTag || inScriptOrStyle || !Character.isWhitespace(c)) {
                    builder.append(c);
                } 
            }
            if (c == '>' && !inScriptOrStyle) {
                String currString = curr.toString();
                if (currString.startsWith("<script")
                        || currString.startsWith("<style")) {
                    inScriptOrStyle = true;
                }
                String lower = currString.toLowerCase();
                if (!lower.startsWith("<!doctype")
                        && !lower.startsWith("<html")
                        && !lower.startsWith("<meta")
                        && !lower.startsWith("<input")
                        && !lower.startsWith("<br")
                        && !lower.startsWith("<img")) {
                    if (!currString.startsWith("</")) {
                        ++depth;
                    }
                }
                if (Character.isWhitespace(builder.charAt(builder.length() - 1))) {
                    do {
                        builder.setLength(builder.length() - 1);
                    } while (Character.isWhitespace(builder.charAt(builder.length() - 1)));
                }
                builder.append('\n');
                for (int j = 0; j < depth * indentSize; ++j) {
                    builder.append(' ');
                }
                builder.append(currString);
                curr.setLength(0);
                addToCurr = false;
                if (i + 1 < text.length() && text.charAt(i+1) != '<') {
                    builder.append('\n');
                    ++depth;
                    for (int j = 0; j < depth * indentSize; ++j) {
                        builder.append(' ');
                    }
                    --depth;
                }
                if (currString.startsWith("</")) {
                    --depth;
                }
            }
        }
        return builder.toString();
    }
}