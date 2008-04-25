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
