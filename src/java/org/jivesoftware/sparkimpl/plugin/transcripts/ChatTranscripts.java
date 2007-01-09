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
import org.jivesoftware.spark.util.URLFileSystem;
import org.jivesoftware.spark.util.log.Log;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
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
final public class ChatTranscripts {

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

        // Write Full Transcript
        writeToFile(transcriptFile, transcript.getMessages());

        // Write to current history File
        final File currentHistoryFile = getCurrentHistoryFile(jid);
        writeToFile(currentHistoryFile, transcript.getNumberOfEntries(20));
    }

    private static void writeToFile(File transcriptFile, Collection<HistoryMessage> messages) {
        final StringBuilder builder = new StringBuilder();

        // Handle new transcript file.
        if (!transcriptFile.exists()) {
            builder.append("<transcript><messages>");
        }

        for (HistoryMessage m : messages) {
            builder.append("<message>");
            builder.append("<to>").append(m.getTo()).append("</to>");
            builder.append("<from>").append(m.getFrom()).append("</from>");
            builder.append("<body>").append(m.getBody()).append("</body>");

            String dateString = FORMATTER.format(m.getDate());
            builder.append("<date>").append(dateString).append("</date>");
            builder.append("</message>");
        }

        if (!transcriptFile.exists()) {
            builder.append("</messages></transcript>");
        }


        if (!transcriptFile.exists()) {
            // Write out new File
            try {
                FileOutputStream fout = new FileOutputStream(transcriptFile);
                OutputStreamWriter ow = new OutputStreamWriter(fout, "UTF-8");
                ow.write(builder.toString());
                ow.close();
            }
            catch (IOException e) {
                Log.error(e);
            }
            return;
        }

        // Append to File
        try {
            RandomAccessFile raf = new RandomAccessFile(transcriptFile, "rw");

            // Seek to end of file
            raf.seek(transcriptFile.length() - 24);

            builder.append("</messages></transcript>");

            // Append to the end
            raf.writeBytes(builder.toString());
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

        final String contents = URLFileSystem.getContents(transcriptFile);
        try {
            MXParser parser = new MXParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(new StringReader(contents));
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
        return new File(SparkManager.getUserDirectory(), "transcripts/" + jid + ".xml");
    }

    /**
     * Returns the current transcript (20 messages) for a particular jid.
     *
     * @param jid the jid of the user.
     * @return the current transcript file.
     */
    public static File getCurrentHistoryFile(String jid) {
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
                message.setBody(parser.nextText());
            }
            else if (eventType == XmlPullParser.START_TAG && "date".equals(parser.getName())) {
                Date d = null;
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