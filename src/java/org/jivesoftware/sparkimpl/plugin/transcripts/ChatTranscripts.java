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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatTranscripts {
    private static Map<String, ChatTranscript> TRANSCRIPTS = new HashMap<String, ChatTranscript>();
    private static Map<String, ChatTranscript> CURRENT_TRANSCRIPTS = new HashMap<String, ChatTranscript>();

    private static DateFormat FORMATTER;

    static {
        FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
    }

    private ChatTranscripts() {

    }

    public static ChatTranscript getChatTranscript(String jid) {
        ChatTranscript transcript = TRANSCRIPTS.get(jid);
        if (transcript == null) {
            final File transcriptFile = getTranscriptFile(jid);
            transcript = load(transcriptFile);
            TRANSCRIPTS.put(jid, transcript);
        }
        return transcript;
    }

    public static ChatTranscript getCurrentChatTranscript(String jid) {
        ChatTranscript transcript = CURRENT_TRANSCRIPTS.get(jid);
        if (transcript == null) {
            final File transcriptFile = getCurrentHistoryFile(jid);
            transcript = load(transcriptFile);
            CURRENT_TRANSCRIPTS.put(jid, transcript);
        }
        return transcript;
    }

    public static void addChatTranscript(String jid, ChatTranscript transcript) {
        TRANSCRIPTS.put(jid, transcript);
    }

    public static void saveTranscript(String jid) {
        try {
            File transcriptFile = getTranscriptFile(jid);
            transcriptFile.getParentFile().mkdirs();
            ChatTranscript transcript = getChatTranscript(jid);
            if (transcript.getMessages().size() == 0) {
                return;
            }

            FileOutputStream fout = new FileOutputStream(transcriptFile);

            Element root = DocumentHelper.createElement("transcript");
            Element messages = root.addElement("messages");

            for (HistoryMessage m : transcript.getMessages()) {
                Element message = messages.addElement("message");

                message.addElement("to").setText(m.getTo());
                message.addElement("from").setText(m.getFrom());
                message.addElement("body").setText(m.getBody());

                String dateString = FORMATTER.format(m.getDate());
                message.addElement("date").setText(dateString);
            }

            ChatTranscript t = new ChatTranscript();
            for(HistoryMessage mes : transcript.getNumberOfEntries(20)){
                t.addHistoryMessage(mes);
            }
            CURRENT_TRANSCRIPTS.put(jid, t);


            try {
                // Write out main transcript
                OutputStreamWriter ow = new OutputStreamWriter(fout, "UTF-8");
                XMLWriter saxWriter = new XMLWriter(ow);
                saxWriter.write(root);
                saxWriter.flush();
                saxWriter.close();

                // Write out current transcript
                List list = messages.elements();
                int size = list.size();
                if (list.size() > 20) {
                    for (int i = 0; i < size - 20; i++) {
                        final Element ele = (Element)list.get(i);
                        messages.remove(ele);
                    }
                }

                // Write out current transcript
                fout = new FileOutputStream(getCurrentHistoryFile(jid));

                ow = new OutputStreamWriter(fout, "UTF-8");
                saxWriter = new XMLWriter(ow);
                saxWriter.write(root);
                saxWriter.flush();
                saxWriter.close();
            }
            catch (IOException e) {
                Log.error(e);
            }

        }
        catch (Exception e) {
            Log.error("Error saving settings.", e);
        }


    }

    private static ChatTranscript load(File transcriptFile) {
        final ChatTranscript transcript = new ChatTranscript();

        if (!transcriptFile.exists()) {
            return transcript;
        }


        try {
            FileInputStream fis = new FileInputStream(transcriptFile);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            SAXReader saxReader = new SAXReader();
            Document pluginXML = null;
            try {
                pluginXML = saxReader.read(isr);
            }
            catch (DocumentException e) {
                Log.error(e);
                return transcript;
            }


            List messages = pluginXML.selectNodes("/transcript/messages/message");
            Iterator iter = messages.iterator();

            while (iter.hasNext()) {
                HistoryMessage message = new HistoryMessage();


                try {
                    Element messageElement = (Element)iter.next();

                    String to = messageElement.selectSingleNode("to").getText();
                    String from = messageElement.selectSingleNode("from").getText();
                    String body = messageElement.selectSingleNode("body").getText();
                    String date = messageElement.selectSingleNode("date").getText();

                    message.setTo(to);
                    message.setFrom(from);
                    message.setBody(body);
                    Date d = null;
                    try {
                        d = FORMATTER.parse(date);
                    }
                    catch (ParseException e) {
                        d = new Date();
                    }
                    message.setDate(d);
                    transcript.addHistoryMessage(message);
                }
                catch (Exception ex) {

                }
            }
        }
        catch (Exception e) {
            // Ignore
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


}