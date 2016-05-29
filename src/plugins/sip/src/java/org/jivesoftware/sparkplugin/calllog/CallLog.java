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

package org.jivesoftware.sparkplugin.calllog;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>CallLog</code> class represents a CallLog instance
 * @version 1.0, 28/09/2006
 */

public class CallLog {

    private String username;

    private String numA;

    private String numB;

    private String dateTime;

    private int duration;

    private Type type;

    public CallLog(String username) {
        this.username = username;
    }

    public CallLog() {

    }

    public enum Type {
        dialed, received, missed;

        public String toString() {
            switch (this) {
                case dialed:
                    return "Dialed Calls";
                case received:
                    return "Received Calls";
                case missed:
                    return "Lost Calls";
            }
            return "Unknown";
        }

        public static Type fromDescription(String description) {
            if (description.equals("Dialed Calls"))
                return dialed;
            if (description.equals("Received Calls"))
                return received;
            if (description.equals("Missed Calls"))
                return missed;

            return dialed;
        }
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getNumA() {
        return numA;
    }

    public void setNumA(String numA) {
        this.numA = numA;
    }

    public String getNumB() {
        return numB;
    }

    public void setNumB(String numB) {
        this.numB = numB;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
