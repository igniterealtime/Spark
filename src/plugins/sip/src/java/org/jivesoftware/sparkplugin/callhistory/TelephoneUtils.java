/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2007 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkplugin.callhistory;

public class TelephoneUtils {


    private TelephoneUtils() {
    }

    public static String removeInvalidChars(String number) {
        if (number == null) {
            return null;
        }

        for (String str : new String[]{"-", "(", ")", " ", "+", "[", "]"})
            number = number.replace(str, "");

        return number;
    }

    public static String formatPattern(String number, String pattern) {

        StringBuffer str = new StringBuffer();
        number = removeInvalidChars(number);

        for (int i = 0, j = 0; i < number.length(); j++) {
            if (j < pattern.length()) {
                char c = pattern.charAt(j);
                if (c == 'x' || c == 'X')
                    str.append(number.charAt(i++));
                else
                    str.append(c);
            } else {
                str.append(number.charAt(i++));
            }
        }

        return str.toString();
    }

    public static void main(String args[]) {

        System.out.println(TelephoneUtils.formatPattern("0(34-325-)5223478", "x(xx)xxxx-xxxx"));
        System.out.println(TelephoneUtils.formatPattern("503-([972])-7215", "(xxx)xxx-xxxx"));

    }
}