/**
 * $RCSfile: ByteFormat.java,v $
 * $Revision: 1.6 $
 * $Date: 2005/04/12 19:17:41 $
 *
 * Copyright (C) 1999-2002 Jive Software. All rights reserved.
 *
 * This software is the proprietary information of Jive Software. Use is subject to license terms.
 */

package org.jivesoftware.spark.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * A formatter for formatting byte sizes. For example, formatting 12345 byes results in
 * "12.1 K" and 1234567 results in "1.18 MB".
 *
 * @author Bill Lynch
 */
public class ByteFormat extends Format {

    public ByteFormat() {
    }

    // Implemented from the Format class

    /**
     * Formats a long which represent a number of bytes.
     */
    public String format(long bytes) {
        return super.format(bytes);
    }

    /**
     * Formats a long which represent a number of kilobytes.
     *
     * @param kilobytes Long kbytes to format as a string.
     * @return String representation of kbytes.
     */
    public String formatKB(long kilobytes) {
        return super.format(kilobytes * 1024);
    }

    /**
     * Format the given object (must be a Long).
     *
     * @param obj assumed to be the number of bytes as a Long.
     * @param buf the StringBuffer to append to.
     * @param pos field position.
     * @return A formatted string representing the given bytes in more human-readable form.
     */
    public StringBuffer format(Object obj, StringBuffer buf, FieldPosition pos) {
        if (obj instanceof Long) {
            long numBytes = (Long) obj;
            if (numBytes < 1024) {
                DecimalFormat formatter = new DecimalFormat("#,##0");
                buf.append(formatter.format((double)numBytes)).append(" bytes");
            }
            else if (numBytes < 1024 * 1024) {
                DecimalFormat formatter = new DecimalFormat("#,##0.0");
                buf.append(formatter.format((double)numBytes / 1024.0)).append(" K");
            }
            else if (numBytes < 1024 * 1024 * 1024) {
                DecimalFormat formatter = new DecimalFormat("#,##0.0");
                buf.append(formatter.format((double)numBytes / (1024.0 * 1024.0))).append(" MB");
            }
            else {
                DecimalFormat formatter = new DecimalFormat("#,##0.0");
                buf.append(formatter.format((double)numBytes / (1024.0 * 1024.0 * 1024.0))).append(" GB");
            }
        }
        return buf;
    }

    /**
     * In this implementation, returns null always.
     *
     * @param source Source string to parse.
     * @param pos Position to parse from.
     * @return returns null in this implementation.
     */
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }
}
