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
package org.jivesoftware.fastpath.workspace.search;

import org.jivesoftware.smackx.ReportedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.fastpath.FpRes;

public class ChatSearchResult {
    private final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
    private String sessionID;
    private Date creationDate;
    private int relevance;

    private String question;
    private String customerName;
    private String email;

    private List fields = new ArrayList();

    public ChatSearchResult(ReportedData.Row row, String query) {
        UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        String startDate = getFirstValue(row, "startDate");

        try {
            creationDate = UTC_FORMAT.parse(startDate);
        }
        catch (ParseException e) {
            Log.error(e);
        }


        sessionID = getFirstValue(row, "sessionID");

        StringBuffer authors = new StringBuffer();
        Iterator athrs = row.getValues("agentJIDs");
        while (athrs.hasNext()) {
            authors.append((String)athrs.next());
            authors.append(" ");
        }

        String rell = getFirstValue(row, "relevance");
        Double o = Double.valueOf(rell);

        relevance = ((int)o.doubleValue() * 100);

        question = getFirstValue(row, "question");
        customerName = getFirstValue(row, "username");
        email = getFirstValue(row, "email");

        fields.add(customerName);
        fields.add(question);
        fields.add(email);
        fields.add(authors.toString());
        fields.add(creationDate);
    }



    public String getFirstValue(ReportedData.Row row, String key) {
        try {
            final Iterator iter = row.getValues(key);
            while (iter.hasNext()) {
                return (String)iter.next();
            }
        }
        catch (Exception e) {
        }
        return null;
    }

    public String getSummary() {
        final StringBuffer buf = new StringBuffer();
        buf.append(FpRes.getString("chat.with") + " ");
        buf.append(customerName);
        buf.append(" ");
        if (question != null) {
            buf.append(FpRes.getString("question") + ": ");
            buf.append(question);
        }
        return buf.toString();
    }

    public String getUsername(){
        return customerName;
    }

    public String getQuestion(){
        return question;
    }

    public Date getStartDate(){
        return creationDate;
    }

    public String getSessionID(){
        return sessionID;
    }

}
