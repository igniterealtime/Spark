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