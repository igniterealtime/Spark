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

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;


/**
 *
 */
public class HistoryCall {
    private String callerName;

    private String number;

    private String groupName;

    private long time;

    private long callLength;

    public HistoryCall(){
        
    }

    public HistoryCall(String callerName, String number, String groupName, long time, long callLength) {
        this.callerName = callerName;
        this.number = number;
        this.groupName = groupName;
        this.time = time;
        this.callLength = callLength;
    }

    public String getCallerName() {
        if (callerName.equals(number)) {
            return getNumber();
        }
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getNumber() {
        return TelephoneUtils.formatPattern(number, PhoneRes.getIString("phone.numpattern"));
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public long getCallLength() {
        return callLength;
    }

    public void setCallLength(long callLength) {
        this.callLength = callLength;
    }
}
