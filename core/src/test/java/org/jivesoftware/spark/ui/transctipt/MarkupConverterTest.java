package org.jivesoftware.spark.ui.transctipt;

import org.junit.Test;

import static org.junit.Assert.*;

public class MarkupConverterTest {

    @Test
    public void rawHtmlToMarkup() {
        String html = "<p><em>ITALIC TEXT</em>\n" +
            "<span style='font-weight: bold;'>BOLD TEXT</span>\n" +
            "<span style='text-decoration: underline;'>UNDERLINED TEXT</span>\n" +
            "<span style='text-decoration: line-through;'><span style='font-size: large;'>BIG STRIKETHROUGH TEXT</span></span>\n" +
            "</p>" +
            "<p>" +
            "<span style='color: #C05C5C;'>COLORED TEXT</span><br/>\n" +
            "ON THE NEXT LINE\n" +
            "</p>" +
            "<strong>BOLD</strong>" +
            "<code>CODE</code>" +
            "<pre>PREFORATTED</pre>";
        String expectedMarkup = "_ITALIC TEXT_\n" +
            "*BOLD TEXT*\n" +
            "*UNDERLINED TEXT*\n" +
            "~*BIG STRIKETHROUGH TEXT~*\n" +
            "\n" +
            "*COLORED TEXT*\n" +
            "\n" +
            "ON THE NEXT LINE\n" +
            "\n" +
            "*BOLD*`CODE`\n" +
            "```\n" +
            "PREFORATTED\n" +
            "```\n";
        String markup = MarkupConverter.rawHtmlToMarkup(html);
        assertEquals(expectedMarkup, markup);
        String expectedMd = "*ITALIC TEXT*\n" +
            "**BOLD TEXT**\n" +
            "__UNDERLINED TEXT__\n" +
            "~~__BIG STRIKETHROUGH TEXT~~__\n" + // it should be __~~ but the regexp is not nested
            "\n" +
            "__COLORED TEXT__\n" +
            "\n" +
            "ON THE NEXT LINE\n" +
            "\n" +
            "**BOLD**" +
            "`CODE`" +
            "\n" +
            "```\n" +
            "PREFORATTED\n" +
            "```\n";
        String md = MarkupConverter.rawHtmlToMarkdown(html);
        assertEquals(expectedMd, md);
    }
}
