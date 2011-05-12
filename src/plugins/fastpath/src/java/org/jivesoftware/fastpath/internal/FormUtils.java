/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
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

import org.jivesoftware.smack.packet.Message;

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
    final public static boolean isNotNull(String str) {
        if (str != null && str.trim().length() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Checks to see if the String is boolean value and will return the appropriate
     * value
     *
     * @param str - the String to check
     * @return true if the string is not null and the value is equal to true, false
     *         otherwise.
     */
    final public static boolean isTrue(String str) {
        return (str != null && str.equalsIgnoreCase("true"));
    }


    /**
     * Checks to see if the data is applicable to be added to metadata.
     *
     * @param data the data to check for validity.
     * @return true if the data is valid.
     */
    public static boolean isValidData(String data) {
        char[] chars = data.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
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
    public static final String replace(String string, String oldString, String newString) {
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
            StringBuffer buf = new StringBuffer(string2.length);
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
        String urlToPush = null;
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
    public static String getNickname(Message message) {
        String from = org.jivesoftware.smack.util.StringUtils.parseResource(message.getFrom());
        return from;
    }

    /**
     * Returns better looking time String.
     * @param seconds the number of seconds to calculate.
     */
    public static String getTimeFromLong(long seconds) {
        final String HOURS = "h";
        final String MINUTES = "min";
        final String SECONDS = "sec";

        final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
        final long MS_IN_AN_HOUR = 1000 * 60 * 60;
        final long MS_IN_A_MINUTE = 1000 * 60;
        final long MS_IN_A_SECOND = 1000;
        Date currentTime = new Date();
        long numDays = seconds / MS_IN_A_DAY;
        seconds = seconds % MS_IN_A_DAY;
        long numHours = seconds / MS_IN_AN_HOUR;
        seconds = seconds % MS_IN_AN_HOUR;
        long numMinutes = seconds / MS_IN_A_MINUTE;
        seconds = seconds % MS_IN_A_MINUTE;
        long numSeconds = seconds / MS_IN_A_SECOND;
        seconds = seconds % MS_IN_A_SECOND;
        long numMilliseconds = seconds;

        StringBuffer buf = new StringBuffer();
        if (numHours > 0) {
            buf.append(numHours + " " + HOURS + ", ");
        }

        if (numMinutes > 0) {
            buf.append(numMinutes + " " + MINUTES);
        }

        String result = buf.toString();

        if (numMinutes < 1) {
            result = "less than 1 minute";
        }

        return result;
    }


}