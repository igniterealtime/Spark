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
package org.jivesoftware.fastpath.workspace.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestUtils {
    private Map metadata;

    public RequestUtils(Map requestData) {
        this.metadata = requestData;
    }

    public Map getMetadata() {
        return metadata;
    }

    /**
     * Returns a Question if one was asked.
     *
     * @return the question the visitor asked before joining the queue. If not question
     *         was asked, null will be returned.
     */
    public String getQuestion() {
        if (getMetadata() == null) {
            return null;
        }

        final String question = getFirstValue("question"); //NOTRANS
        return question;
    }

    /**
     * Returns the nickname of the Visitor.
     *
     * @return the nickname of the visitor.
     */
    public String getUsername() {
        if (getMetadata() == null) {
            return null;
        }

        final String vistorName = getFirstValue("username");//NOTRANS
        return vistorName;
    }

    /**
     * Return the Users Email address, if specified.
     *
     * @return the users email address, otherwise will return null if an email
     *         address was not specified.
     */
    public String getEmailAddress() {
        if (getMetadata() == null) {
            return null;
        }

        final String emailAddress = getFirstValue("email");//NOTRANS
        return emailAddress;
    }

    /**
     * Return the request location.
     *
     * @return the url (string format) of where the user made the initial request.
     */
    public String getRequestLocation() {
        if (getMetadata() == null) {
            return null;
        }

        final String requestLocation = getFirstValue("Location");//NOTRANS
        return requestLocation;
    }

    /**
     * Returns the Unique Identifier of the user.
     *
     * @return the unique id of the user.
     */
    public String getUserID() {
        if (getMetadata() == null) {
            return null;
        }

        final String userID = getFirstValue("userID");//NOTRANS
        return userID;
    }

    public String getInviter() {
        if (getMetadata() == null) {
            return null;
        }

        final String inviter = getFirstValue("inviter");//NOTRANS
        return inviter;
    }

    public String getWorkgroup() {
        if (getMetadata() == null) {
            return null;
        }

        final String workgroup = getFirstValue("workgroup");//NOTRANS
        return workgroup;
    }

    public boolean isTransfer() {
        if (getMetadata() == null) {
            return false;
        }

        boolean isTransfer = Boolean.valueOf(getFirstValue("transfer")).booleanValue();//NOTRANS
        return isTransfer;
    }

    public boolean isInviteOrTransfer() {
        if (getMetadata() == null) {
            return false;
        }

        return getMetadata().containsKey("transfer"); //NOTRANS
    }

    public String getSessionID() {
        if (getMetadata() == null) {
            return null;
        }

        final String workgroup = getFirstValue("sessionID");//NOTRANS
        return workgroup;
    }

    public Map getMap() {
        final Map returnMap = new HashMap(metadata);
        returnMap.remove("sessionID");
        returnMap.remove("transfer");
        returnMap.remove("workgroup");
        returnMap.remove("inviter");
        returnMap.remove("username");
        returnMap.remove("question");
        returnMap.remove("userID");
        //returnMap.remove("email");
        return returnMap;
    }

    public String getValue(String key) {
        return getFirstValue(key);
    }

    private String getFirstValue(String key) {
        Object o = getMetadata().get(key);
        if (o instanceof List) {
            final List list = (List)getMetadata().get(key);
            if (list.size() > 0) {
                return (String)list.get(0);
            }
        }
        else if (o instanceof String) {
            return (String)o;
        }
        return null;
    }

}
