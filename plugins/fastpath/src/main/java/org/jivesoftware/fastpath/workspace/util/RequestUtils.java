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
package org.jivesoftware.fastpath.workspace.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;


public class RequestUtils {
    private final Map<String, List<String>> metadata;

    public RequestUtils(Map<String, List<String>> requestData) {
        this.metadata = requestData;
    }

    public Map<String, List<String>> getMetadata() {
        return metadata;
    }

    /**
     * Returns a Question if one was asked.
     *
     * @return the question the visitor asked before joining the queue. If not question
     *         was asked, null will be returned.
     */
    public String getQuestion() {
        return getMetadata() == null ? null : getFirstValue("question");
    }

    /**
     * Returns the nickname of the Visitor.
     *
     * @return the nickname of the visitor.
     */
    public String getUsername() {
        return getMetadata() == null ? null : getFirstValue("username");
    }

    /**
     * Return the Users Email address, if specified.
     *
     * @return the users email address, otherwise will return null if an email
     *         address was not specified.
     */
    public String getEmailAddress() {
        return getMetadata() == null ? null : getFirstValue("email");
    }

    /**
     * Return the request location.
     *
     * @return the url (string format) of where the user made the initial request.
     */
    public String getRequestLocation() {
        return getMetadata() == null ? null : getFirstValue("Location");
    }

    /**
     * Returns the Unique Identifier of the user.
     *
     * @return the unique id of the user or {@code null}.
     */
    public EntityBareJid getUserID() {
        return getMetadata() == null ? null : JidCreate.entityBareFromUnescapedOrThrowUnchecked(
            Objects.requireNonNull(getFirstValue("userID")));
    }

    public String getInviter() {
        return getMetadata() == null ? null : getFirstValue("inviter");
    }

    public String getWorkgroup() {
        return getMetadata() == null ? null : getFirstValue("workgroup");
    }

    public boolean isTransfer() {
        return getMetadata() != null && Boolean.parseBoolean(getFirstValue("transfer"));
    }

    public boolean isInviteOrTransfer() {
        if (getMetadata() == null) {
            return false;
        }

        return getMetadata().containsKey("transfer"); //NOTRANS
    }

    public String getSessionID() {
        return getMetadata() == null ? null : getFirstValue("sessionID");
    }

    public Map<String, List<String>> getMap() {
        final Map<String, List<String>> returnMap = new HashMap<>(metadata);
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
            final List<?> list = (List<?>) o;
            if (list.size() > 0) {
                return (String) list.get(0);
            }
        }
        else if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

}
