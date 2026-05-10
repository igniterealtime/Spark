package org.jivesoftware.spark.ui.transctipt;

import org.jivesoftware.smackx.message_markup.element.CodeBlockElement;
import org.jivesoftware.smackx.message_markup.element.MarkupElement;
import org.jivesoftware.smackx.message_markup.element.SpanElement;
import org.jivesoftware.smackx.message_markup.element.SpanElement.SpanStyle;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.jivesoftware.smackx.message_markup.element.SpanElement.SpanStyle.*;
import static org.jivesoftware.spark.ui.transctipt.MarkupConverter.applySpans;
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

    @Test
    public void testApplySpans() {
        String text = "plain bold italic code strikethrough\n" +
            "code block\n" +
            "\n" +
            "* list 1\n" +
            "* list 2\n" +
            "\n" +
            "> quote";
        Set<SpanStyle> codeEm = new LinkedHashSet<>(2);
        codeEm.add(emphasis);
        codeEm.add(code);
        List<MarkupElement.MarkupChildElement> spans = List.of(
            new SpanElement(6, 10, Set.of()),
            new SpanElement(11, 17, codeEm),
            new SpanElement(18, 22, Set.of(code)),
            new SpanElement(23, 36, Set.of(deleted)),
            new CodeBlockElement(37, 47)
        );

        String formatted = applySpans(text, spans);
        String expected = "plain *bold* _`italic`_ `code` ~strikethrough~\n" +
            "\n" +
            "```\n" +
            "code block\n" +
            "```\n" +
            "\n" +
            "\n" +
            "* list 1\n" +
            "* list 2\n" +
            "\n" +
            "> quote";
        assertEquals(expected, formatted);
    }
}
