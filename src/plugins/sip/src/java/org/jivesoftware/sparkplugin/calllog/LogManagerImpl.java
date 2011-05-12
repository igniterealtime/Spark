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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import net.java.sipmack.common.Log;
import net.java.sipmack.sip.Call;
import net.java.sipmack.sip.event.CallRejectedEvent;
import net.java.sipmack.sip.event.CallStateEvent;
import net.java.sipmack.sip.event.MessageEvent;
import net.java.sipmack.sip.event.UnknownMessageEvent;
import net.java.sipmack.softphone.SoftPhone;
import net.java.sipmack.softphone.SoftPhoneManager;
import net.java.sipmack.softphone.listeners.RegisterEvent;
import net.java.sipmack.softphone.listeners.SoftPhoneListener;

import org.jivesoftware.Spark;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.TaskEngine;
import org.jivesoftware.sparkplugin.callhistory.HistoryCall;
import org.jivesoftware.sparkplugin.sipaccount.SipAccountPacket;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

/**
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 *         <p/>
 *         The <code>RemoteLog</code> class manages the remote logging
 * @version 1.0, 28/09/2006
 */

public class LogManagerImpl implements SoftPhoneListener, LogManager {

    private boolean remoteLogging;

    private SoftPhone softPhone;

    private List<HistoryCall> calls = new ArrayList<HistoryCall>();

    public LogManagerImpl(SoftPhone softPhone) {
        this.softPhone = softPhone;

        // Load call history.
        loadCallHistory();

        softPhone.addSoftPhoneListener(this);
    }

    public boolean isRemoteLogging() {
        return remoteLogging;
    }

    public void setRemoteLogging(boolean remoteLogging) {
        this.remoteLogging = remoteLogging;
    }


    public void callStateChanged(final CallStateEvent evt) {
        if (evt.getNewState().equals(Call.DISCONNECTED)) {

            TimerTask task = new TimerTask() {
                public void run() {
                    checkForMissedCalls(evt.getSourceCall());
                }
            };

            TaskEngine.getInstance().schedule(task, 1000);
        }
    }

    public void callRejectedRemotely(CallRejectedEvent evt) {
        //Do Nothing
    }

    private void checkForMissedCalls(Call call) {
        String party = call.getAddress().split(":")[1].split("@")[0];

        String numA = call.isIncoming() ? party : softPhone.getUsername();
        String numB = call.isIncoming() ? softPhone.getUsername() : party;

        CallLog.Type type;
        if (call.getElapsedTime() == 0 && call.isIncoming()) {
            type = CallLog.Type.missed;
        }
        else {
            type = call.isIncoming() ? CallLog.Type.received : CallLog.Type.dialed;
        }

        HistoryCall history = new HistoryCall(call.getRemoteName(), call.getNumber(), type.toString(), new Date().getTime(), call.getElapsedTime());
        calls.add(history);
        commit();

        if (type == CallLog.Type.missed) {
            // Show missed calls
            SoftPhoneManager.getInstance().getMissedCalls().addMissedCall(call.getRemoteName(), call.getNumber());
        }

        if (isRemoteLogging()) {
            try {
                CallLogExtension e = new CallLogExtension();

                e.setValue("numA", numA);
                e.setValue("numB", numB);
                e.setValue("duration", String
                        .valueOf(call.getElapsedTime()));

                e.setValue("datetime",DateFormat.getInstance().format(new Date()));
                e.setValue("type", type.name());

                LogPacket.logEvent(SparkManager.getConnection(), e);

            }
            catch (XMPPException e) {
                Log.error("RemoteLogging", e);
            }
        }
    }

    public void messageReceived(MessageEvent evt) {
	
    }

    public void receivedUnknownMessage(UnknownMessageEvent evt) {

    }

    public void registerStatusChanged(RegisterEvent evt) {
        if (isRemoteLogging()) {
            try {
                SipAccountPacket.setSipRegisterStatus(SparkManager
                        .getConnection(), evt.getStatus());
            }
            catch (Exception e) {
                Log.error("registerStatusChanged", e);
            }
        }
    }

    public void showCallHistory() {

    }

    private File getHistoryFile() {
        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, "spark-phone-history.xml");
    }

    public void commit() {
        final StringBuilder builder = new StringBuilder();

        builder.append("<calls>");

        for (HistoryCall m : calls) {
            builder.append("<call>");
            builder.append("<callerName>").append(m.getCallerName()).append("</callerName>");
            builder.append("<number>").append(m.getNumber()).append("</number>");
            builder.append("<groupName>").append(m.getGroupName()).append("</groupName>");
            builder.append("<time>").append(m.getTime()).append("</time>");
            builder.append("<callLength>").append(m.getCallLength()).append("</callLength>");
            builder.append("</call>");
        }

        builder.append("</calls>");

        // Write out new File
        try {
            getHistoryFile().getParentFile().mkdirs();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getHistoryFile()), "UTF-8"));
            out.write(builder.toString());
            out.close();
        }
        catch (IOException e) {
            org.jivesoftware.spark.util.log.Log.error(e);
        }
    }

    public Collection<HistoryCall> getCallHistory() {
        return calls;
    }


    /**
     * Reads in the transcript file using the Xml Pull Parser.
     */
    private void loadCallHistory() {
        File historyFile = getHistoryFile();
        if (!historyFile.exists()) {
            return;
        }

        // Otherwise load
        try {
            final MXParser parser = new MXParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(historyFile), "UTF-8"));
            parser.setInput(in);
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG && "call".equals(parser.getName())) {
                    calls.add(getHistoryCall(parser));
                }
                else if (eventType == XmlPullParser.END_TAG && "calls".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

    private static HistoryCall getHistoryCall(XmlPullParser parser) throws Exception {
        HistoryCall call = new HistoryCall();

        // Check for nickname
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG && "callerName".equals(parser.getName())) {
                call.setCallerName(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && "number".equals(parser.getName())) {
                call.setNumber(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && "groupName".equals(parser.getName())) {
                call.setGroupName(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && "time".equals(parser.getName())) {
                call.setTime(Long.parseLong(parser.nextText()));
            }
            else if (eventType == XmlPullParser.START_TAG && "callLength".equals(parser.getName())) {
                call.setCallLength(Long.parseLong(parser.nextText()));
            }
            else if (eventType == XmlPullParser.END_TAG && "call".equals(parser.getName())) {
                done = true;
            }
        }

        return call;
    }

    public void deleteCall(HistoryCall call) {
   	 getCallHistory().remove( call );
    }
    
}
