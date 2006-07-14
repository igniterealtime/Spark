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

import com.thoughtworks.xstream.XStream;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class ChatTranscripts {
    private static Map transcripts = new HashMap();
    private static XStream xstream = new XStream();

    private ChatTranscripts() {
    }

    static {
        xstream.alias("transcript", ChatTranscript.class);
        xstream.alias("message", HistoryMessage.class);
    }


    public static ChatTranscript getChatTranscript(String jid) {
        ChatTranscript transcript = (ChatTranscript)transcripts.get(jid);
        if (transcript == null) {
            transcript = load(jid);
            transcripts.put(jid, transcript);
        }
        return transcript;
    }

    public static void addChatTranscript(String jid, ChatTranscript transcript) {
        transcripts.put(jid, transcript);
    }

    public static void saveTranscript(String jid) {

        try {
            File file = getTranscriptFile(jid);
            file.getParentFile().mkdirs();
            ChatTranscript transcript = getChatTranscript(jid);
            if (transcript.getMessages().size() == 0) {
                file.delete();
                return;
            }

            FileOutputStream fout = new FileOutputStream(getTranscriptFile(jid));
            OutputStreamWriter ow = new OutputStreamWriter(fout, "UTF-8");


            xstream.toXML(transcript, ow);
        }
        catch (Exception e) {
            Log.error("Error saving settings.", e);
        }
    }

    private static ChatTranscript load(String jid) {
        File transcriptFile = getTranscriptFile(jid);
        try {
            FileInputStream fis = new FileInputStream(transcriptFile);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            return (ChatTranscript)xstream.fromXML(isr);
        }
        catch (Exception e) {
            // Ignore
        }
        return new ChatTranscript();
    }

    /**
     * Returns the settings file.
     *
     * @return the settings file.
     */
    public static File getTranscriptFile(String jid) {
        return new File(SparkManager.getUserDirectory(), "transcripts/" + jid + ".xml");
    }


}