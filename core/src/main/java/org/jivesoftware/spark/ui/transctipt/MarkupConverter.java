package org.jivesoftware.spark.ui.transctipt;


import org.apache.commons.lang3.StringEscapeUtils;
import org.jivesoftware.smackx.message_markup.element.CodeBlockElement;
import org.jivesoftware.smackx.message_markup.element.ListElement;
import org.jivesoftware.smackx.message_markup.element.MarkupElement;
import org.jivesoftware.smackx.message_markup.element.SpanElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MarkupConverter {

    /**
     * Pidgin 2 and Psi sends formatted text as XHTML-IM body with style in &lt;span&gt; and we should replace them.
     */
    private static final Pattern[] KNOWN_HTML_PATTERNS = new Pattern[]{
        Pattern.compile("<span style='font-weight: bold;'>(.*?)</span>", Pattern.MULTILINE), // Pidgin 2
        Pattern.compile("<span style='.?text-decoration: underline;'>(.*?)</span>", Pattern.MULTILINE), // Pidgin 2, Psi. The underline becomes just bold
        Pattern.compile("<span style='.?text-decoration: line-through;'>(.*?)</span>", Pattern.MULTILINE), // Pidgin 2, Psi.
        Pattern.compile("<span style='font-size: large;'>(.*?)</span>", Pattern.MULTILINE), // Pidgin 2. The big font becomes bold
        Pattern.compile("<span style='font-size: small;'>(.*?)</span>", Pattern.MULTILINE), // Pidgin 2. Just remove small, no MD equivalent
        Pattern.compile("<em>(.*?)</em>", Pattern.MULTILINE), // Pidgin 2
        Pattern.compile("<strong>(.*?)</strong>", Pattern.MULTILINE), // general HTML
        Pattern.compile("<br/>", Pattern.MULTILINE), // general HTML
        Pattern.compile("<p>", Pattern.MULTILINE), // general HTML
        Pattern.compile("</p>", Pattern.MULTILINE), // general HTML
        Pattern.compile("<code>(.*?)</code>", Pattern.MULTILINE), // general HTML
        Pattern.compile("<pre>(.*?)</pre>", Pattern.MULTILINE), // general HTML
        Pattern.compile("<a href='(.*?)'>(.*?)</a>", Pattern.MULTILINE), // Pidgin 2
        Pattern.compile("<span style='.?color:.?#.*;'>(.*?)</span>", Pattern.MULTILINE), // Pidgin 2, Psi. The colored text will be just bold
        Pattern.compile("<span style='background: #.*;'>(.*?)</span>", Pattern.MULTILINE), // Pidgin 2
        Pattern.compile("<span style=' font-weight:600;'>(.*?)</span>", Pattern.MULTILINE), // Psi
        Pattern.compile("<span style=' font-style:italic;'>(.*?)</span>", Pattern.MULTILINE), // Psi
        Pattern.compile("<span style=' background-color:#.*;'>(.*?)</span>", Pattern.MULTILINE), // Psi
        Pattern.compile("<b>(.*?)</b>", Pattern.MULTILINE), // general HTML
        Pattern.compile("<i>(.*?)</i>", Pattern.MULTILINE), // general HTML
    };

    private static final String[] PIDGIN2_PATTERNS_REPLACE_MD = new String[]{
        "**$1**",
        "__$1__",
        "~~$1~~",
        "__$1__",
        " $1 ",
        "*$1*",
        "**$1**",
        "\n",
        "",
        "\n",
        "`$1`",
        "\n```\n$1\n```\n",
        "[$2]($1)",
        "__$1__",
        "__$1__",
        "**$1**",
        "*$1*",
        "*$1*",
        "**$1**",
        "*$1*",
    };

    private static final String[] PIDGIN2_PATTERNS_REPLACE_MARKUP_XEP0393 = new String[]{
        "*$1*",
        "*$1*",
        "~$1~",
        "*$1*",
        "$1",
        "_$1_",
        "*$1*",
        "\n",
        "",
        "\n",
        "`$1`",
        "\n```\n$1\n```\n",
        "$2 ($1)",
        "*$1*",
        "*$1*",
        "*$1*",
        "_$1_",
        "_$1_",
        "*$1*",
        "_$1_",
    };


    public static String rawHtmlToMarkup(String html) {
        return rawHtmlToMarkup(html, PIDGIN2_PATTERNS_REPLACE_MARKUP_XEP0393);
    }

    public static String rawHtmlToMarkdown(String html) {
        return rawHtmlToMarkup(html, PIDGIN2_PATTERNS_REPLACE_MD);
    }

    static String rawHtmlToMarkup(String html, String[] replacements) {
        String markup = html;
        for (int i = 0; i < KNOWN_HTML_PATTERNS.length; i++) {
            Pattern p = KNOWN_HTML_PATTERNS[i];
            String replacement = replacements[i];
            markup = p.matcher(markup).replaceAll(replacement);
        }
        @SuppressWarnings("deprecation")
        String unescaped = StringEscapeUtils.unescapeHtml4(markup);
        return unescaped;
    }

    public static String applySpans(String text, List<MarkupElement.MarkupChildElement> spans) {
        // Tags to insert at each position
        Map<Integer, List<String>> openingTags = new HashMap<>();
        Map<Integer, List<String>> closingTags = new HashMap<>();
        for (var span : spans) {
            if (span instanceof SpanElement) {
                SpanElement spanEl = (SpanElement) span;
                if (spanEl.getStyles().isEmpty()) { // Dino IM leaves empty style for strong
                    addTags(span, openingTags, "*", closingTags, "*");
                }
                for (SpanElement.SpanStyle style : spanEl.getStyles()) {
                    switch (style) {
                        case emphasis:
                            addTags(span, openingTags, "_", closingTags, "_");
                            break;
                        case code:
                            addTags(span, openingTags, "`", closingTags, "`");
                            break;
                        case deleted:
                            addTags(span, openingTags, "~", closingTags, "~");
                            break;
                        default: // strong
                            addTags(span, openingTags, "*", closingTags, "*");
                            break;
                    }
                }
            } else if (span instanceof CodeBlockElement) {
                addTags(span, openingTags, "\n```\n", closingTags, "\n```\n");
            } else if (span instanceof ListElement) {
                // not supported
            }
        }

        StringBuilder result = new StringBuilder(text.length() + 200);
        for (int i = 0; i <= text.length(); i++) {
            // Insert closing tags before character at position i
            List<String> closes = closingTags.get(i);
            if (closes != null) {
                for (String tag : closes) {
                    result.append(tag);
                }
            }
            // Insert opening tags before character at position i
            List<String> opens = openingTags.get(i);
            if (opens != null) {
                for (String tag : opens) {
                    result.append(tag);
                }
            }
            // Append character
            if (i < text.length()) {
                result.append(text.charAt(i));
            }
        }
        return result.toString();
    }

    private static void addTags(MarkupElement.MarkupChildElement span, Map<Integer, List<String>> openingTags, String startTag, Map<Integer, List<String>> closingTags, String endTag) {
        openingTags.computeIfAbsent(span.getStart(), k -> new ArrayList<>()).add(startTag);
        // endPos is exclusive
        closingTags.computeIfAbsent(span.getEnd(), k -> new ArrayList<>()).add(0, endTag); // prepend for proper nesting
    }

}
