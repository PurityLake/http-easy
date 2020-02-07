package ie.httpeasy.utils;

public class HtmlFormatter {
    private static int indentSize = 1;
    public static String formatText(String text) {
        boolean _isMetaTag = false;
        boolean _isEndTag = false;
        boolean skip = false;
        StringBuilder builder = new StringBuilder(text.length());
        int depth = 0;
        int i, j;
        char c;
        for (i = 0; i < text.length(); ++i) {
            c = text.charAt(i);
            switch (c) {
                case '<':
                    String tag = "";
                    for (j = i + 1; j < text.length() 
                        && text.charAt(j) != ' '
                        && text.charAt(j) != '>'; ++j) {
                        tag += text.charAt(j);
                    }
                    if (isScriptTag(tag) || isStyleTag(tag)) {
                        skip = !tag.startsWith("/");
                        i = j;
                    }
                    if (isMetaTag(tag)) {
                        _isMetaTag = true;
                    }
                    if (!skip) {
                        if (text.charAt(i + 1) == '/') {
                            _isEndTag = true;
                            --depth;
                            builder.append('\n');
                            for (j = 0; j < indentSize * depth; ++j) {
                                builder.append(' ');
                            }
                        }
                        builder.append(c);
                    }
                    break;
                case '>':
                    if (!skip) {
                        if (!_isMetaTag && !_isEndTag) {
                            ++depth;
                        } else {
                            _isMetaTag = false;
                            _isEndTag = false;
                        }
                        builder.append(c);
                        builder.append('\n');
                        for (j = 0; j < indentSize * depth; ++j) {
                            builder.append(' ');
                        }
                    }
                    break;
                default:
                    if (!skip) {
                        builder.append(c);
                        break;
                    }
            }
        }
        return builder.toString();
    }

    private static boolean isScriptTag(String text) {
        return text.contains("script");
    }

    private static boolean isStyleTag(String text) {
        return text.contains("style");
    }

    private static boolean isMetaTag(String text) {
        return text.contains("meta");
    }

    private static String escapeToSymbol(String escape) {
        switch (escape) {
            case "lt":
                return "<";
            case "gt":
                return ">";
            case "amp":
                return "&";
            default:
                return null;
        }
    }
}