package ie.httpeasy.utils;

public class HtmlFormatter {
    private static int indentSize = 1;
    public static String formatText(String text) {
        boolean inScriptOrStyle = false;
        StringBuilder builder = new StringBuilder(text.length());
        boolean addToCurr = false;
        StringBuilder curr = new StringBuilder(1024);
        int depth = 0;
        char c;

        for (int i = 0; i < text.length(); ++i) {
            c = text.charAt(i);
            if (c == '<') {
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
                builder.append(c);
                if (c == '\n') {
                    ++depth;
                    for (int j = 0; j < depth * indentSize; ++j) {
                        builder.append(' ');
                    }
                    --depth;
                }
            }
            if (c == '>' && !inScriptOrStyle) {
                String currString = curr.toString();
                if (currString.startsWith("<script")
                        || currString.startsWith("<style")) {
                    inScriptOrStyle = true;
                }
                if (!currString.startsWith("<!doctype")
                        && !currString.startsWith("<html")
                        && !currString.startsWith("<meta")
                        && !currString.startsWith("<input")
                        && !currString.startsWith("<br")
                        && !currString.startsWith("<img")) {
                    if (!currString.startsWith("</")) {
                        ++depth;
                    } 
                    //++depth;
                    lastTagNoDepth = false;
                } else {
                    lastTagNoDepth = true;
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