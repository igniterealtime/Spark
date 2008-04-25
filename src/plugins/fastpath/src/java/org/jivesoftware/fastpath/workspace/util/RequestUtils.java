/**
 * $RCSfile: ,v $
 * $Revision: 1.0 $
 * $Date: 2005/05/25 04:20:03 $
 *
 * Copyright (C) 1999-2008 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
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
