package org.jivesoftware.spark.util;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class StringUtilsTest {
    @Test
    public void testEscapeHTMLTags() {
        assertNull(StringUtils.escapeHTMLTags(null));
        assertEquals("hello", StringUtils.escapeHTMLTags("hello"));
        assertEquals("&lt;b&gt;&quot;test&quot;&lt;/b&gt;", StringUtils.escapeHTMLTags("<b>\"test\"</b>"));
    }

    @Test
    public void testHighlightWords() {
        assertNull(StringUtils.highlightWords(null, new String[]{"test"}, "<b>", "</b>"));
        assertEquals("hello world", StringUtils.highlightWords("hello world", new String[0], "<b>", "</b>"));
        String highlighted = StringUtils.highlightWords("This is a test message", new String[]{"test"}, "<b>", "</b>");
        assertEquals("This is a <b>test</b> message", highlighted);
    }

    @Test
    public void testEscapeAndUnescapeXML() {
        assertNull(StringUtils.escapeForXML(null));
        assertNull(StringUtils.unescapeFromXML(null));
        String raw = "<msg to=\"user\" from='me'>Hello & welcome</msg>";
        String escaped = StringUtils.escapeForXML(raw);
        assertEquals("&lt;msg to=&quot;user&quot; from='me'>Hello &amp; welcome&lt;/msg>", escaped);
        assertEquals(raw, StringUtils.unescapeFromXML(escaped));
    }

    @Test
    public void testMakeFirstWordCapital() {
        assertEquals("", StringUtils.makeFirstWordCapital(""));
        assertEquals("A", StringUtils.makeFirstWordCapital("a"));
        assertEquals("Spark", StringUtils.makeFirstWordCapital("spark"));
        assertEquals("Spark", StringUtils.makeFirstWordCapital("Spark"));
    }

    @Test
    public void testModifyWildcards() {
        assertEquals("", StringUtils.modifyWildcards(null));
        System.setProperty("spark.test.prop", "testvalue");
        String modified = StringUtils.modifyWildcards("Value: %property{spark.test.prop}/%");
        assertEquals("Value: testvalue", modified);
    }

    @Test
    public void testGetMD5Checksum() throws IOException {
        Path tempFile = Files.createTempFile("spark-md5-test", ".txt");
        try {
            Files.writeString(tempFile, "hello");
            String checksum = StringUtils.getMD5Checksum(tempFile.toAbsolutePath().toString());
            assertEquals("5d41402abc4b2a76b9719d911017c592", checksum);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    public void testReplaceSpecialLetters() {
        assertEquals("aeiou", StringUtils.replaceSpecialLetters("áéíóú"));
        assertEquals("AEIOU", StringUtils.replaceSpecialLetters("ÁÉÍÓÚ"));
        assertEquals("nNyYеЕ", StringUtils.replaceSpecialLetters("ñÑýÝёЁ"));
    }

    @Test
    public void testReplaceMe() {
        assertEquals("User is smiling", StringUtils.replaceMe("/me is smiling", "User"));
    }
}
