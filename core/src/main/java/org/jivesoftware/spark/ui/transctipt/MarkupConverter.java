package org.jivesoftware.spark.ui.transctipt;


import org.apache.commons.lang3.StringEscapeUtils;

import java.util.regex.Pattern;

public class MarkupConverter {

    /**
     * Pidgin 2 and Psi sends formatted text with this
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
}
