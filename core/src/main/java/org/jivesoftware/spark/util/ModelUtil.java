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


import org.jivesoftware.resource.Res;

import java.util.Iterator;
import java.util.ListIterator;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Utility methods frequently used by data classes and design-time
 * classes.
 */
public final class ModelUtil {

    private ModelUtil() {
        //  Prevent instantiation.
    }


    /**
     * Returns <CODE>true</CODE> if the specified {@link String} is not
     * <CODE>null</CODE> and has a length greater than zero.  This is
     * a very frequently occurring check.
     *
     * @param s String to check
     * @return True if string is null or empty
     */
    public static boolean hasLength(CharSequence s) {
        return isNotBlank(s);
    }


    /**
     * Returns a formatted String from time.
     *
     * @param diff the amount of elapsed time.
     * @return the formatted String.
     */
    public static String getTimeFromLong(long diff) {
        final String DAYS = Res.getString("time.days");
        final String HOURS = Res.getString("time.hours");
        final String MINUTES = Res.getString("time.minutes");
        final String LESS_THAN_ONE_MINUTE = Res.getString("time.less.than.one.minute");

        final long MS_IN_A_DAY = 1000 * 60 * 60 * 24;
        final long MS_IN_AN_HOUR = 1000 * 60 * 60;
        final long MS_IN_A_MINUTE = 1000 * 60;
        long numDays = diff / MS_IN_A_DAY;
        diff = diff % MS_IN_A_DAY;
        long numHours = diff / MS_IN_AN_HOUR;
        diff = diff % MS_IN_AN_HOUR;
        long numMinutes = diff / MS_IN_A_MINUTE;
        if (numMinutes == 0) {
            return LESS_THAN_ONE_MINUTE;
        }

        StringBuilder buf = new StringBuilder();

        if(numDays > 0){
            buf.append(numDays).append(" ").append(DAYS).append(", ");
        }

        if (numHours > 0) {
            buf.append(numHours).append(" ").append(HOURS).append(", ");
        }

        buf.append(numMinutes).append(" ").append(MINUTES);

        String result = buf.toString();
        return result;
    }


    /**
     * Creates an Iterator that is the reverse of a ListIterator.
     *
     * @param i Iterator of a list.
     * @return Reversed iterator.
     */
    public static <T> Iterator<T> reverseListIterator(ListIterator<T> i) {
        return new ReverseListIterator<>( i );
    }
}

/**
 * An Iterator that is the reverse of a ListIterator.
 */
class ReverseListIterator<T> implements Iterator<T> {
    private final ListIterator<T> _i;

    public ReverseListIterator(ListIterator<T> i) {
        _i = i;
        while (_i.hasNext()) _i.next();
    }

    @Override
	public boolean hasNext() {
        return _i.hasPrevious();
    }

    @Override
	public T next() {
        return _i.previous();
    }

    @Override
	public void remove() {
        _i.remove();
    }
}










