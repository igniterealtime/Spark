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

import static org.apache.commons.lang3.StringUtils.replaceChars;

public class TelephoneUtils {

    private TelephoneUtils() {
    }

    /**
     * Parses out the numbers only from a phone number.
     *
     * @param number the full phone number.
     * @return the phone number only (5551212)
     */
    public static String getNumbersFromPhone(String number) {
        String clearNumber = replaceChars(number, "()- ", "");
        if (number.startsWith("1")) {
            clearNumber = clearNumber.substring(1);
        }
        return clearNumber;
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

    public static String formatPhoneNumber(String number) {
        number = number.trim();
        if (number.length() != 10) {
            return number;
        }
        String buf = "";
        buf += "(";
        String areaCode = number.substring(0, 3);
        buf += areaCode;
        buf += ") ";

        String nextThree = number.substring(3, 6);
        buf += " ";
        buf += nextThree;
        buf += "-";

        String lastThree = number.substring(6, 10);
        buf += lastThree;
        return buf;
    }

}
