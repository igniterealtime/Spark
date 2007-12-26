/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.sparkimpl.plugin.transcripts;

import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.util.StringUtils;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;


/**
 * A Utility class that manages the Chat Transcripts within Spark.
 *
 * @author Derek DeMoro
 */
public final class ChatTranscripts {

    /**
     * Default Date Formatter *
     */
    private static DateFormat FORMATTER;

    static {
        FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
    }

    private ChatTranscripts() {

    }

    /**
     * Appends the given ChatTranscript to the transcript file associated with a JID.
     *
     * @param jid        the jid of the user.
     * @param transcript the ChatTranscript.
     */
    public static void appendToTranscript(String jid, ChatTranscript transcript) {
        final File transcriptFile = getTranscriptFile(jid);

        // Write Full Transcript, appending the messages.
        writeToFile(transcriptFile, transcript.getMessages(), true);

        // Write to current history File
        final File currentHistoryFile = getCurrentHistoryFile(jid);
        ChatTranscript tempTranscript = getCurrentChatTranscript(jid);
        for (HistoryMessage message : transcript.getMessages()) {
            tempTranscript.addHistoryMessage(message);
        }
        writeToFile(currentHistoryFile, tempTranscript.getNumberOfEntries(20), false);
    }

    private static void writeToFile(File transcriptFile, Collection<HistoryMessage> messages, boolean append) {
        final StringBuilder builder = new StringBuilder();

        // Handle new transcript file.
        if (!transcriptFile.exists() || !append) {
            builder.append("<transcript><messages>");
        }

        for (HistoryMessage m : messages) {
            builder.append("<message>");
            builder.append("<to>").append(m.getTo()).append("</to>");
            builder.append("<from>").append(m.getFrom()).append("</from>");
            builder.append("<body>").append(StringUtils.escapeForXML(m.getBody())).append("</body>");

            String dateString = FORMATTER.format(m.getDate());
            builder.append("<date>").append(dateString).append("</date>");
            builder.append("</message>");
        }

        if (!transcriptFile.exists() || !append) {
            builder.append("</messages></transcript>");
        }


        if (!transcriptFile.exists() || !append) {
            // Write out new File
            try {
                transcriptFile.getParentFile().mkdirs();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(transcriptFile), "UTF-8"));
                out.write(builder.toString());
                out.close();
            }
            catch (IOException e) {
                Log.error(e);
            }
            return;
        }

        // Append to File
        try {
            final RandomAccessFile raf = new RandomAccessFile(transcriptFile, "rw");

            // We want to append near the end of the document as the last
            // child in the transcript.
            final String endTag = "</messages></transcript>";
            builder.append(endTag);

            raf.seek(transcriptFile.length() - endTag.length());

            // Append to the end
            raf.write(builder.toString().getBytes("UTF-8"));
            raf.close();
        }
        catch (IOException e) {
            Log.error(e);
        }
    }

    /**
     * Retrieve the current chat history.
     *
     * @param jid the jid of the user whos history you wish to retrieve.
     * @return the ChatTranscript (last 20 messages max).
     */
    public static ChatTranscript getCurrentChatTranscript(String jid) {
        return getTranscript(getCurrentHistoryFile(jid));
    }

    /**
     * Retrieve the full chat history.
     *
     * @param jid the jid of the the user whos history you wish to retrieve.
     * @return the ChatTranscript.
     */
    public static ChatTranscript getChatTranscript(String jid) {
        return getTranscript(getTranscriptFile(jid));
    }

    /**
     * Reads in the transcript file using the Xml Pull Parser.
     *
     * @param transcriptFile the transcript file to read.
     * @return the ChatTranscript.
     */
    public static ChatTranscript getTranscript(File transcriptFile) {
        final ChatTranscript transcript = new ChatTranscript();
        if (!transcriptFile.exists()) {
            return transcript;
        }

        try {
            final MXParser parser = new MXParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(transcriptFile), "UTF-8"));
            parser.setInput(in);
            boolean done = false;
            while (!done) {
                int eventType = parser.next();
                if (eventType == XmlPullParser.START_TAG && "message".equals(parser.getName())) {
                    transcript.addHistoryMessage(getHistoryMessage(parser));
                }
                else if (eventType == XmlPullParser.END_TAG && "transcript".equals(parser.getName())) {
                    done = true;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return transcript;
    }

    /**
     * Returns the settings file.
     *
     * @param jid the
     * @return the settings file.
     */
    public static File getTranscriptFile(String jid) {
        // Unescape Node
        jid = UserManager.unescapeJID(jid);

        return new File(SparkManager.getUserDirectory(), "transcripts/" + jid + ".xml");
    }

    /**
     * Returns the current transcript (20 messages) for a particular jid.
     *
     * @param jid the jid of the user.
     * @return the current transcript file.
     */
    public static File getCurrentHistoryFile(String jid) {
        // Unescape Node
        jid = UserManager.unescapeJID(jid);

        return new File(SparkManager.getUserDirectory(), "transcripts/" + jid + "_current.xml");
    }

    private static HistoryMessage getHistoryMessage(XmlPullParser parser) throws Exception {
        HistoryMessage message = new HistoryMessage();

        // Check for nickname
        boolean done = false;
        while (!done) {
            int eventType = parser.next();
            if (eventType == XmlPullParser.START_TAG && "to".equals(parser.getName())) {
                message.setTo(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && "from".equals(parser.getName())) {
                message.setFrom(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && "body".equals(parser.getName())) {
                message.setBody(StringUtils.unescapeFromXML(parser.nextText()));
            }
            else if (eventType == XmlPullParser.START_TAG && "date".equals(parser.getName())) {
                Date d;
                try {
                    d = FORMATTER.parse(parser.nextText());
                }
                catch (ParseException e) {
                    d = new Date();
                }
                message.setDate(d);
            }
            else if (eventType == XmlPullParser.END_TAG && "message".equals(parser.getName())) {
                done = true;
            }
        }


        return message;
    }


}