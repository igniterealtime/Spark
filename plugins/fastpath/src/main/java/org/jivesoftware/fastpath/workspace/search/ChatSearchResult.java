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
package org.jivesoftware.fastpath.workspace.search;

import org.jivesoftware.smackx.search.ReportedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jivesoftware.spark.util.log.Log;
import org.jivesoftware.fastpath.FpRes;

public class ChatSearchResult {
    private final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
    private final String sessionID;
    private Date creationDate;

    private final String question;
    private final String customerName;
    private final String email;

    private final List<String> fields = new ArrayList<>();

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

        StringBuilder authors = new StringBuilder();
        for ( final CharSequence agentJID : row.getValues("agentJIDs") )
        {
            authors.append(agentJID);
            authors.append(" ");
        }

        question = getFirstValue(row, "question");
        customerName = getFirstValue(row, "username");
        email = getFirstValue(row, "email");

        fields.add(customerName);
        fields.add(question);
        fields.add(email);
        fields.add(authors.toString());
        fields.add(creationDate.toString());
    }



    public String getFirstValue(ReportedData.Row row, String key) {
        final List<CharSequence> values = row.getValues( key );
        if ( values.isEmpty() ) {
            return null;
        }

        return values.get(0).toString();
    }

    public String getSummary() {
        final StringBuilder buf = new StringBuilder();
        buf.append(FpRes.getString("chat.with")).append(" ");
        buf.append(customerName);
        buf.append(" ");
        if (question != null) {
            buf.append(FpRes.getString("question")).append(": ");
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
