/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.jivesoftware.smack.util.MD5;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.nio.file.Files.readAllBytes;
import static java.util.regex.Pattern.quote;

public class StringUtils {
    // Constants used by escapeHTMLTags
    private static final char[] QUOTE_ENCODE = "&quot;".toCharArray();
    private static final char[] AMP_ENCODE = "&amp;".toCharArray();
    private static final char[] LT_ENCODE = "&lt;".toCharArray();
    private static final char[] GT_ENCODE = "&gt;".toCharArray();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private StringUtils() {
    }

    public static String replace(String string, String oldString, String newString) {
        return string.replace(oldString, newString);
    }

    /**
     * This method takes a string which may contain HTML tags (ie, &lt;b&gt;,
     * &lt;table&gt;, etc) and converts the '&lt' and '&gt;' characters to their
     * HTML escape sequences.
     * 
     * @param in
     *            the text to be converted.
     * @return the input string with the characters '&lt;' and '&gt;' replaced
     *         with their HTML escape sequences.
     */
    public static String escapeHTMLTags(String in) {
        if (in == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = in.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int) (len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch <= '>') {
                if (ch == '<') {
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(LT_ENCODE);
                } else if (ch == '>') {
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(GT_ENCODE);
                } else if (ch == '"') {
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(QUOTE_ENCODE);
                }
            }
        }
        if (last == 0) {
            return in;
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        return out.toString();
    }

    /**
     * Highlights words in a string. Words matching ignores case. The actual
     * highlighting method is specified with the start and end highlight tags.
     *
     * @author Mark Imbriaco.
     * @param string
     *            the String to highlight words in.
     * @param words
     *            an array of words that should be highlighted in the string.
     * @param startHighlight
     *            the tag that should be inserted to start highlighting.
     * @param endHighlight
     *            the tag that should be inserted to end highlighting.
     * @return a new String with the specified words highlighted.
     */
    public static String highlightWords(String string, String[] words, String startHighlight, String endHighlight) {
        if (string == null || words == null || words.length == 0) {
            return string;
        }
        StringBuilder regexp = new StringBuilder("(?i)\\b(");
        // Iterate through each word and generate a word list for the regexp.
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                regexp.append('|');
            }
            regexp.append(quote(words[i]));
        }
        regexp.append(')');
        return string.replaceAll(regexp.toString(), startHighlight + "$1" + endHighlight);
    }

    /**
     * Escapes all necessary characters in the String so that it can be used
     * in an XML doc.
     * 
     * @param string
     *            the string to escape.
     * @return the string with appropriate characters escaped.
     */
    public static String escapeForXML(String string) {
        if (string == null) {
            return null;
        }
        char ch;
        int i = 0;
        int last = 0;
        char[] input = string.toCharArray();
        int len = input.length;
        StringBuilder out = new StringBuilder((int) (len * 1.3));
        for (; i < len; i++) {
            ch = input[i];
            if (ch <= '>') {
                if (ch == '<') {
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(LT_ENCODE);
                } else if (ch == '&') {
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(AMP_ENCODE);
                } else if (ch == '"') {
                    if (i > last) {
                        out.append(input, last, i - last);
                    }
                    last = i + 1;
                    out.append(QUOTE_ENCODE);
                } else if (ch != 10 && ch != 13 && ch != 9) {
                    // Disallow all ASCII control characters, except space,
                    // enter characters and tabs:
                    if (ch < 32) {
                        if (i > last) {
                            out.append(input, last, i - last);
                        }
                        last = i + 1;
                    }
                }
            }
        }
        if (last == 0) {
            // Return after escaping <![CDATA]]> end sequence "]]>"
            return string.replace("]]>", "]]&amp;gt;");
        }
        if (i > last) {
            out.append(input, last, i - last);
        }
        // Return after escaping <![CDATA]]> end sequence "]]>"
        return out.toString().replace("]]>", "]]&amp;gt;");
    }

    /**
     * Unescapes the String by converting XML escape sequences back into normal
     * characters.
     * 
     * @param string
     *            the string to unescape.
     * @return the string with appropriate characters unescaped.
     */
    public static String unescapeFromXML(String string) {
        if (string == null) {
            return null;
        }
        string = replace(string, "&lt;", "<");
        string = replace(string, "&gt;", ">");
        string = replace(string, "&quot;", "\"");
        string = replace(string, "&amp;", "&");
        return string;
    }

    /**
     * Converts the first Character of a CharSequence to its Uppercase Instance<br>
     * test -> Test
     * @return String, with first char uppercased
     */
    public static String makeFirstWordCapital(CharSequence cs) {
        if (cs.length() == 0) {
            return "";
        }
        return Character.toUpperCase(cs.charAt(0)) + cs.toString().substring(1);
    }

    /**
     * Modifies Wildcards such as <b>%random%</b>,<b>%system%</b>,
     * <b>%version%</b>,<b>%time%</b> into what they represent
     * <p>
     * for properties see System.getProperties()
     * 
     * @param resource
     *            String to modify
     * @return modified String
     * @author Wolf Posdorfer
     */
    public static String modifyWildcards(String resource) {
        if (resource == null) {
            return "";
        }
        resource = resource.replace("%random%", String.valueOf(Math.round(Math.random() * 1000)));
        resource = resource.replace("%system%", System.getProperty("os.name", "").replace(" ", ""));
        resource = resource.replace("%version%", System.getProperty("os.version", "").replace(" ", ""));
        resource = resource.replace("%time%", LocalTime.now().format(TIME_FORMATTER));

        while (resource.contains("%property{") && resource.contains("}/%")) {
            int start = resource.indexOf("%property{");
            int end = resource.indexOf("}/%", start);
            if (end == -1) {
                break;
            }
            String begin = resource.substring(0, start);
            String propKey = resource.substring(start + "%property{".length(), end);
            String propValue = System.getProperty(propKey, "");
            String rest = resource.substring(end + "}/%".length());
            resource = begin + propValue + rest;
        }
        return resource;
    }

    /**
     * Creates an MD5 Checksum of a File, given the Filepath
     */
    public static String getMD5Checksum(String filepath) throws IOException {
        return MD5.hex(readAllBytes(Path.of(filepath)));
    }

    /**
     * Returns the value of the major/feature element of the version number.
     *
     * @param version A java version String
     * @return The major
     */
    public static int getJavaMajorVersion(String version) {
        if (version.startsWith("1.")) {
            int dot = version.indexOf('.', 2);
            if ( dot == -1 ) {
                version = version.substring( 2 );
            } else {
                version = version.substring( 2, dot );
            }
        } else {
            int dot = version.indexOf('.');
            if (dot != -1) {
                version = version.substring(0, dot);
            }
            int hyphen = version.indexOf('-');
            if (hyphen != -1) {
                version = version.substring(0, hyphen);
            }
        }
        return Integer.parseInt(version);
    }

    /**
     * Returns the string with replaced special letter
     * Allow to search contacts without special letters interfering
     *
     * @param text A input string
     * @return String with replaced special letter
     */
    public static String replaceSpecialLetters(String text) {
        return text
                .replaceAll("[áâàåãä]", "a")
                .replaceAll("[ÁÂÀÅÃÄ]", "A")
                .replaceAll("[éêèë]", "e")
                .replaceAll("[ÉÊÈË]", "E")
                .replaceAll("[íîìі]", "i")
                .replaceAll("[ÍÎÌІ]", "I")
                .replaceAll("[óôòõö]", "o")
                .replaceAll("[ÓÔÒÕÖ]", "O")
                .replaceAll("[úûùü]", "u")
                .replaceAll("[ÚÛÙÜ]", "U")
                .replace('ñ', 'n')
                .replace('Ñ', 'N')
                .replace('ý', 'y')
                .replace('Ý', 'Y')
                .replace('ё', 'е')
                .replace('Ё', 'Е');
    }

    public static String replaceMe(String msg, String nickname) {
        msg = nickname + msg.substring("/me".length());
        return msg;
    }
}
