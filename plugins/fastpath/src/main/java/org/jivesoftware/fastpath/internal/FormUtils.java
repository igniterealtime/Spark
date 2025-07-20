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
package org.jivesoftware.fastpath.internal;

import org.jivesoftware.resource.Res;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.parts.Resourcepart;

import java.util.Date;

/**
 * The <code>WebUtils</code> class is a utility class for some of the most
 * mundane procedures in the WebChat client and in servlet programming. This
 * will be moved over to a more suitable class down the line. Probably be handled
 * in the com.jivesoftware.web.utils package to be used throughout Jive.
 */
final public class FormUtils {
    private FormUtils() {
    }

    /**
     * Check to see if string has been assigned a value. This is generally used
     * in web applications/applet when a user request a parameter from the parameter stack.
     *
     * @param str - the string to check.
     * @return true if String has been assigned a value, false otherwise.
     */
    public static boolean isNotNull(String str) {
        return str != null && str.trim().length() > 0;
    }

    /**
     * Checks to see if the String is boolean value and will return the appropriate
     * value
     *
     * @param str - the String to check
     * @return true if the string is not null and the value is equal to true, false
     *         otherwise.
     */
    public static boolean isTrue(String str) {
        return Boolean.parseBoolean(str);
    }


    /**
     * Checks to see if the data is applicable to be added to metadata.
     *
     * @param data the data to check for validity.
     * @return true if the data is valid.
     */
    public static boolean isValidData(String data) {
        char[] chars = data.toCharArray();
        for (char c : chars) {
            if (Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }



    /**
     * Replaces all instances of oldString with newString in string.
     *
     * @param string    the String to search to perform replacements on
     * @param oldString the String that should be replaced by newString
     * @param newString the String that will replace all instances of oldString
     * @return a String will all instances of oldString replaced by newString
     */
    public static String replace(String string, String oldString, String newString) {
        if (string == null) {
            return null;
        }
        // If the newString is null or zero length, just return the string since there's nothing
        // to replace.
        if (newString == null) {
            return string;
        }
        int i = 0;
        // Make sure that oldString appears at least once before doing any processing.
        if ((i = string.indexOf(oldString, i)) >= 0) {
            // Use char []'s, as they are more efficient to deal with.
            char[] string2 = string.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuilder buf = new StringBuilder(string2.length);
            buf.append(string2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            // Replace all remaining instances of oldString with newString.
            while ((i = string.indexOf(oldString, i)) > 0) {
                buf.append(string2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(string2, j, string2.length - j);
            return buf.toString();
        }
        return string;
    }

    /**
     * Validate the given text - to pass it must contain letters, digits, '@', '-', '_', '.', ','
     * or a space character.
     *
     * @param text the text to check
     * @return true if the given text is valid, false otherwise.
     */
    public boolean validateChars(String text) {
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isLetterOrDigit(ch) && ch != '@' && ch != '-' && ch != '_'
                    && ch != '.' && ch != ',' && ch != ' ') {
                return false;
            }
        }
        return true;
    }


    public static String getPushedURL(String body) {
        String urlToPush;
        int index = body.indexOf("]");
        urlToPush = body.substring(index + 1);
        int index2 = urlToPush.indexOf("http://");
        int httpsIndex = urlToPush.indexOf("https");
        if (index2 == -1 && httpsIndex == -1) {
            urlToPush = "http://" + urlToPush;
        }

        return urlToPush;
    }

    /**
     * Returns the nickname of the user who sent the message.
     *
     * @param message the message sent.
     * @return the nickname of the user who sent the message.
     */
    public static Resourcepart getNickname(Message message) {
        return message.getFrom().getResourceOrThrow();
    }

}
